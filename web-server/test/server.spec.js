const { request } = require("./utils");
const app = require("../index");
const database = require("../database")
const { expect } = require("chai");


describe("controllers", () => {
    function testBook(book) {
        expect(book.id).to.be.a('number');
        expect(book.title).to.be.a('string');
        expect(book.image).to.be.a('string');
        expect(book.isbn).to.be.a('string');
    }

    const server = app.nativeServer;

    before(() => {
        app.setDebug(false);
    })

    describe("GET /api/books", () => {
        it("gets all the books in a batch", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/books")
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body).not.to.be.null;
            expect(body.data).to.be.an('array')
                .that.has.a.lengthOf(20)
                .that.is.lessThan(30);

            const book = body.data[0]

            testBook(book);
        })


        it("gets all the books that matches a search", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books?search=n")
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body).not.to.be.null;
            expect(body.data).to.be.an('array').that.is.not.empty;
        })

        it("gets books according to the batch and size", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books?size=2&batch=1")
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body.size).to.be.eq(2);
            expect(body.data.length).to.be.eq(2);
            expect(body.batch).to.be.eq(1);
        })

        it("returns an empty array if not book search doesn't exists", async() => {
            const {body, statusCode} = await request(server)
                .route("/api/books?search=WRONG_SEARCH_KEY")
                .send();

            expect(statusCode).to.be.eq(404);
            expect(body).not.to.be.null;
            expect(body.data).to.be.an('array').that.is.empty;
        })
    })

    describe("GET /api/books/:id", () => {
        it("it gets a book that matches the id", async () => {
            const isbn = "0241512425";

            const {body, statusCode} = await request(server)
                .route(`/api/books/${isbn}`)
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body.title).not.to.be.empty;
            expect(body.description).to.a('string').that.is.not.empty;
            expect(body.image).to.be.a('string').that.is.not.empty;
            expect(body.isbn).to.be.a('string').that.is.eq(isbn);
            expect(body.prices).to.be.an('array').that.is.not.empty;
            expect(body.genres).to.be.an('array').that.is.not.empty;
            expect(body.authors).to.be.an('array').that.is.not.empty;
        })

        it("returns a 404 when the book isn't found", async () => {
            const {body, statusCode} = await request(server)
                .route("/api/books/WRONG_ISBN")
                .send();

            expect(statusCode).to.be.eq(404);
            expect(body).to.be.an('object').that.is.empty;
        })
    })

    describe("GET /api/genres", () => {
        it("it gets all the available genre", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/genres")
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body).to.be.an('array').that.is.not.empty;

            const genre = body[0];

            expect(genre.id).to.be.a('number');
            expect(genre.title).to.be.a('string');

        })
    })

    describe("GET /api/genres/:id/books", () => {
        it("it gets all the book in a genre", async () => {
            const id = 1

            const { body, statusCode } = await request(server)
                .route(`/api/genres/${id}/books`)
                .send();

            expect(statusCode).to.be.eq(200)
            expect(body.id).to.be.eq(id);
            expect(body.title).to.be.a('string');
            expect(body.books).to.be.an('array');

            const book = body.books[0]
            testBook(book);
        })

        it("returns a 404 when the book doesn't exist", async () => {
            const id = "wrongGenreId"

            const { body, statusCode } = await request(server)
                .route(`/api/genres/${id}/books`)
                .send();

            expect(statusCode).to.be.eq(404);
            expect(body).to.be.an('object').that.is.empty;
        })
    })

    describe("GET /api/authors", () => {
        it("it gets all the available author", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/authors")
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body).to.be.an('array').that.is.not.empty;

            const author = body[0];

            expect(author.id).to.be.a('number');
            expect(author.name).to.be.a('string');
        })
    })

    describe("GET /api/authors/:id/books", () => {
        it("it gets all the book in a author", async () => {
            const id = 1;

            const { body, statusCode } = await request(server)
                .route(`/api/authors/${id}/books`)
                .send();

            expect(statusCode).to.be.eq(200);
            expect(body.id).to.be.eq(1);
            expect(body.name).to.be.a('string').that.is.not.empty;
            expect(body.books).to.be.an('array').that.is.not.empty;

            const book = body.books[0]
            testBook(book);
        })

        it("returns a 404 when the author doesn't exist", async () => {
            const { body, statusCode } = await request(server)
                .route("/api/authors/${id}/books")
                .send();

            expect(statusCode).to.be.eq(404);
            expect(body).to.be.an('object').that.is.empty;
        })
    })
})