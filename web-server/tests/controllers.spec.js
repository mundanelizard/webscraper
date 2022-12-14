const { request } = require("./utils");
const { nativeServer: server } = require("../index");
const database = require("../database")
const { expect } = require("chai");


decribe("controllers", () => {
    afterAll(() => {
        // kills the database after all the tests.
        database.end();
    })

    describe("GET /api/books", () => {
        it("gets all the books in a batch", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/books")
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).to.be(20);
        })

        it("gets all the books that matches a search", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books")
                .query({ search: "one" })
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).not.to.be(0);
        })

        it("gets books according to the batch and size", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books")
                .query({ size: 2, batch: 0 })
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).to.be(2);
        })

        it("returns an empty array if not book search doesn't exists", async() => {
            const {body, statusCode} = await request(server)
                .route("/api/books")
                .query({ search: "WRONG_SEARCH_QUERY" })
                .send();

            expect(statusCode).to.be(404)
            expect(body.length).to.be(2);
        })
    })

    describe("GET /api/books/:id", () => {
        it("it gets a book that matches the id", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books/:isbn")
                .send();

            expect(statusCode).to.be(200);
            expect(body.isbn).to.be(isbn);
        })

        it("returns a 404 when the book isn't found", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books/WRONG_ISBN")
                .send();

            expect(statusCode).to.be(404);
            expect(body).to.be(null);
        })
    })

    describe("GET /api/genres", () => {
        it("it gets all the available genre", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/genres")
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).not.to.be(0);
        })
    })

    describe("GET /api/genres/:id/books", () => {
        it("it gets all the book in a genre", async () => {
            const id = "genreId"

            const { body, statusCode } = await request(server)
                .route(`/api/genres/${id}/books`)
                .send();

            expect(statusCode).to.be(200)
            expect(body.id).to.be(id);
        })

        it("returns a 404 if no book exists in the genre", async () => {
            const id = "wrongGenreId"

            const { body, statusCode } = await request(server)
                .route(`/api/genres/${id}/books`)
                .send();

            expect(statusCode).to.be(200);
            expect(body).to.be(null);
        })
    })

    describe("GET /api/authors", () => {
        it("it gets all the authors in the books", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/authors")
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).not.to.be(0);
        })
    })

    describe("GET /api/authors/:id/books", () => {
        it("it gets all the book in a author", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/authors/${id}/books")
                .send();

            expect(statusCode).to.be(200);
            expect(body.length).not.to.be(0);
        })

        it("returns a 404 if no book exists in the author", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/authors/${id}/books")
                .send();

            expect(statusCode).to.be(200);
            expect(body).to.be(null);
        })
    })
})