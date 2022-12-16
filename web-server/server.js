const http = require("http");
const { URLSearchParams } = require("url");
const path = require("path");
const fs = require("fs");
const mime = require("mime");

let servers = [];

function defaultHandler(req, res) {
  res.send("not found thank you");
}

function staticHandler(req, res, basePath) {
  let url;
  if (req.url === "/") url = path.join(basePath, "index.html");
  else url = path.join(basePath, path.normalize(req.url));

  const exists = fs.existsSync(url);

  if (!exists) {
    url = path.join(basePath, "/index.html");
  }

  fs.readFile(url, function (err, data) {
    if (err) {
      res.writeHead(404);
      res.end(JSON.stringify(err));
      return;
    }

    const mimeType = mime.getType(url);
    res.writeHead(200, {
      "Content-Type": mimeType,
    });
    res.end(data);
  });
}

/**
 * Parses general path to get a parser that extracts params.
 * @param {string} path path you want to extract regex parser from.
 * @returns a regex parser.
 */
function decodePath(path) {
  const keys = [];

  path = path.replace(/:(\w+)/g, (_, key) => {
    keys.push(key);
    return "([^\\/]+)";
  });

  const source = `^(${path})`;

  const regex = new RegExp(source, "i");
  return { regex, keys };
}

/**
 * Checks if a path is equal to a location and returns the url param
 * @param {string} path paths you want your location match
 * @param {string} location location you match to match against a path
 * @param {boolean} exact exact matches isn't greedy
 * @returns null if it's not a match, {} is there aren't any params and object with params as the key if there are params
 */
function matchRoute(path, location, exact = true) {
  const { regex, keys } = decodePath(path);
  const match = location.match(regex);

  if (!match) return null;

  if (exact && match[0] !== match.input) return null;

  const params = match.slice(2);

  return keys.reduce((collection, param, index) => {
    collection[param] = params[index];
    return collection;
  }, {});
}

/**
 * Creates a new 'tiny-server' server and returns the object.
 * @returns a tiny server object. 
 */
function server(options) {
  const bindings = { default: defaultHandler };
  const server = http.createServer();
  let debug = options.debug;
  servers.push(server);

  server.on("request", (request, response) => {
    response.closed = false;
    response.send = (data, statusCode = 200) => send(data, statusCode);
    response.sendFile = (path) => sendFile(path);

    const { url } = request;

    // parses incoming data into something usable.
    const querySearchParams = new URLSearchParams(url.split("?")[1]);
    const path = url.split("?")[0].replace(/^\/|\/$/g, "");

    const query = {};
    querySearchParams.forEach((key, value) => {
      query[value] = key;
    });

    const body = [];

    // builds up the body content
    request.on("data", (chunk) => {
      body.push(chunk);
    });

    // converts the body in to a string and adds it to the database
    request.on("end", () => {
      request.body = Buffer.concat(body).toString();
    });

    // handles error that occurs when processing request data.
    request.on("error", (error) => {
      console.error(error.stack);
    });

    // setting default headers, can be overridden in handler function
    response.setHeader("Content-Type", "application/json");
    response.setHeader("X-Powered-By", "tiny-server");

    const send = (data, statusCode) => {
      response.closed = true;
      response.statusCode = statusCode;

      // response.end(data) can be replaced with
      // response.write(data) then response.end()
      // anyone works, but I think this takes fewer lines.
      if (!data) {
        response.end();
      } else if (typeof data === "string") {
        response.end(data);
      } else {
        response.end(JSON.stringify(data));
      }
    };

    const sendFile = (url) => {
      fs.readFile(url, function (err, data) {
        if (err) {
          response.writeHead(404);
          response.end(JSON.stringify(err));
          return;
        }

        const mimeType = mime.getType(url);
        response.writeHead(200, {
          "Content-Type": mimeType,
        });
        response.end(data);
      });
    }

    // adds extra data to the request and response object
    request.path = path;
    request.query = query;

    // setting handler to 'notfound' route by default.
    let handler = bindings["default"];

    // checks if path matches any bindings
    for (const route in bindings) {
      const params = matchRoute(route, path);

      if (params) {
        request.params = params;
        handler = bindings[route];
        break;
      }
    }

    if (debug) {
      console.log("DEBUG:", request.method, request.url);
    }

    // handles the incoming request
    const result = handler(request, response);

    if (!result || !result.then) {
        return;
    }

    // handles the edge case when the handler is async
    result
        .then(function(result) {
          if (response.closed) return;

          if (result instanceof AsyncHandlerResult) {
            response.send(result.data, result.status);
            return;
          }

          const statusCode = 200;
          response.send(result, statusCode)
        })
        .catch(function(error) {
          if (error && error.statusCode === 500) {
            console.log("AsyncHandlerError: ", error.message, error);
          }

          if (response.closed) return;

          if (error instanceof AsyncHandlerError) {
            response.send(error.data, error.statusCode)
            return;
          }

          console.log("ServerError: ", error);
        })
  })

  // handles all server errors by logging them out to the console.
  // a better approach to could be taken, but this works.
  server.on("error", (err) => {
    console.log(err.stack);
  });

  const closeServer = (cb) => {
    server.close(cb);
    servers = servers.filter(s => server !== s);
  }

  const serveStatic = (basePath) => {
    if (!basePath || typeof basePath !== 'string') {
      throw new Error("'server.serve' expected a 'basedPath' of type 'string' but instead received " + typeof basePath)
    }

    bindings["default"] = (req, res)  => staticHandler(req, res, basePath);
  }

  // adds new route to bindings.
  const route = (path, func) => (bindings[path.replace(/^\/|\/$/g, "")] = func);
  // starts the server up.
  const listen = (...args) => server.listen(...args);

  return {
    route,
    listen,
    nativeServer: server,
    default: (handler) => {bindings["default"] = handler},
    static: serveStatic,
    close: closeServer,
    setDebug: (d) => debug = d,
  };
}

class AsyncHandlerError extends Error {
  constructor(message, data, statusCode) {
    super(message);
    this.data = data;
    this.statusCode = statusCode;
  }
}

class AsyncHandlerResult {
  constructor(data, statusCode) {
    this.data = data;
    this.statusCode = statusCode;
  }
}

server.decodePath = decodePath;
server.defaultHanlder = defaultHandler;
server.matchRoute = matchRoute;
server.getServer = () => [...servers];
server.AsyncHandlerError = AsyncHandlerError;
server.AsyncHandlerResult = AsyncHandlerResult;

// terminates all running server before exiting.
process.on("beforeExit", () => {
  console.log("Terminating server(s)...");
  servers.forEach(server => server.close())
  console.log("Successfully terminated server(s)");
})

module.exports = server;
