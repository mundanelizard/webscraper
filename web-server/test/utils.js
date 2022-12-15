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

    _query = {};

    constructor(server) {
        this.server = server;
    }

    route(endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    setBody(data) {
        this.data = data;
        return this;
    }

    query(query) {
        this._query = query;
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
            query: this._query,
        });

        const response = httpMock.createResponse();
        this.server.emit("request", request, response);
        await new Promise((resolve) => setTimeout(resolve, 500))

        const rawData = response._getData();

        return {
            status: response._getStatusMessage(),
            statusCode: response._getStatusCode(),
            body: JSON.parse(rawData),
            headers: response._getHeaders(),
        };
    }
}

module.exports = {
    ServerSimulator,
    request: (server) => new ServerSimulator(server),
};
