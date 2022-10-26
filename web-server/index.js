const server = require("./server");
const {
  getCameras,
  getCamera,
  searchCameras,
} = require("./controller/cameras");
const path = require("path");
const mime = require("mime");
const fs = require("fs");
const app = server();

// it routes all undefined APIs to a static server.
// it's is basically a static file server.
app.default((req, res) => {
  let url;
  if (req.url === "/") url = path.join(__dirname, "/public/index.html");
  else url = path.join(__dirname, "/public/" + path.normalize(req.url));

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
});

app.route("/api/cameras", async (req, res) => {
  const start = parseInt(req.query.start) || 0;
  const step = parseInt(req.query.step) || 20;

  const response = {};
  let statusCode = 400;

  try {
    const result = await getCameras(start, step);
    // add pagination details
    response.result = result;
    statusCode = 200;
  } catch (error) {
    console.log(error);
    response.result = [];
    statusCode = 500;
  }

  res.send(response, statusCode);
});

app.route("/api/cameras/:id", async (req, res) => {
  const id = req.params.id;
  const cameraId = parseInt(id);

  const response = {};
  let statusCode = 400;

  try {
    const result = await getCamera(cameraId);
    response.result = result;
    statusCode = 200;
  } catch (error) {
    console.log(error);
    response.result = [];
    statusCode = 500;
  }

  res.send(response, statusCode);
});

app.listen(8000, () => {
  console.log("Listening at port 8000");
});

app.route("/api/search", async (req, res) => {
  const start = parseInt(req.query.start) || 0;
  const step = parseInt(req.query.step) || 20;
  const { search = "" } = req.query;

  const response = {};
  let statusCode = 400;

  try {
    const result = await searchCameras(start, step, search);
    response.result = result;
    statusCode = 200;
  } catch (error) {
    console.log(error);
    response.result = [];
    statusCode = 500;
  }

  res.send(response, statusCode);
});
