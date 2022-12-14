const server = require("./server");
const { AsyncHandlerError } = require("./server");
const {
  getBooks,
  getBook,
  getAuthors,
  getBooksByAuthor,
  getGenres,
  getBooksByGenre,
} = require("./controllers");
const path = require("path");
const app = server({ debug: true });

app.static(path.join(__dirname, "/public/"));

app.route("/api/books", async (req) => {
  const batch = parseInt(req.query.batch) || 1;
  const size = parseInt(req.query.size) || 20;
  const search = req.query.search;

  const books = await getBooks(batch, size, search);

  if (books.total === 0) {
    throw new AsyncHandlerError("Result not found", books, 404);
  }

  return books;
});

app.route("/api/books/:id", async (req) => {
  const id = req.params.id;

  const book = await getBook(id);

  if (!book) {
    throw new AsyncHandlerError("Book with " + id + " couldn't be found", {}, 404);
  }

  return book;
});

app.route("/api/authors", async () => {
  const authors = await getAuthors();

  if (!authors) {
    throw new AsyncHandlerError("Couldn't find authors", [], 404);
  }

  return authors;
})

app.route("/api/authors/:id/books", async (req) => {
  const id = req.params.id;

  const authorBooks = await getBooksByAuthor(id);

  if (!authorBooks) {
    throw new AsyncHandlerError("Couldn't find author books", {}, 404);
  }

  return authorBooks;
})

app.route("/api/genres", async () => {
  const genres = await getGenres();

  if (genres.length === 0) {
    throw new AsyncHandlerError("Couldn't find genres", [], 404);
  }

  return genres;
})

app.route("/api/genres/:id/books", async(req) => {
  const id = req.params.id;
  const books = await getBooksByGenre(id);

  if (!books) {
    throw new AsyncHandlerError("Couldn't find books by genre", {}, 404);
  }

  return books;
})


if (require.main.filename === __filename) {
  app.listen(8000, () => console.log("Listening at port 8000"));
}

module.exports = app;
