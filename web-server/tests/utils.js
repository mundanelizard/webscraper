const { Buffer } = require("buffer");
const httpMock = require("node-mocks-http")

// /**
//  * Simulates a request on a tiny server.
//  * @param server {TinyServer} a tiny server instance - tiny server is the wrapper around http.Server in the project.
//  * @returns a new instance of ServerSimulator
//  */

class ServerSimulator {
    data = null;
    endpoint = null;

    query = {};

    constructor(server) {
        this.server = server;
    }

    route(endpoint) {
        this.endpoint = endpoint;
        this.setData = (data) => this.data = data;
        return this;
    }

    query(query) {
        this.query = query;
        return this;
    }

    async send(data) {
        if(this.data && data) {
            console.warn("Overriding previously set data");
        }

        if (data) {
            this.data = data;
        }

        const request = httpMock.createRequest({
            hostname: 'www.example.com',
            port: 80,
            path: this.endpoint,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: data,
            query: this.query,
        });

        const response = httpMock.createResponse();
        this.server.emit("request", request, response);
        await new Promise((resolve) => setTimeout(resolve, 500))

        const rawData = response._getData();

        return {
            status: response._getStatusMessage(),
            statusCode: response._getStatusCode(),
            body: rawData,
            headers: response._getHeaders(),
        };
    }

    _drainStream(stream) {
        return new Promise((resolve, reject) => {
            const chunks = [];
            stream.on('data', (chunk) => {
                chunks.push(Buffer.from(chunk))
                console.log("Receiving the data", chunk)
            });
            stream.on('error', (err) => reject(err));
            stream.on('end', () => {
                console.log("Ending the stream");
                resolve(Buffer.concat(chunks).toString('utf8'))
            });
        })
    }
}

module.exports = {
    ServerSimulator,
    request: (endpoint) => new ServerSimulator(endpoint),
};
