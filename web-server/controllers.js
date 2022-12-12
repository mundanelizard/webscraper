const database = require("./database");

function asyncQuery(query) {
  return new Promise(function (resolve, reject) {
    database.query(query, function(error, result) {
      if (error) {
        return reject(error);
      }

      if (!Array.isArray(result)) {
        return reject("Expected 'result' to be an 'array' but received " + typeof result);
      }

      resolve(result);
    })
  })
}


/**
 * Gets all books in the database.
 * @param {Number} batch where to start from
 * @param {Number} size number of results to return.
 * @param {String} search number
 * @returns a promise that resolves to `null` if nothing was found or an array of results.
 */
async function getBooks(batch, size, search) {
  if (typeof batch !== "number" || typeof size !== "number") {
    throw new TypeError("Expected 'start' and 'step' to be of type 'number'.");
  }

  if (batch <= 0) {
    batch = 1;
  }

  const interjection = (!search ? "" : ` WHERE title LIKE ${database.escape('%'+search+'%')}`);

  // TODO: How to tell the count of a query while only return a few segment from it?
  // TODO: How to join both request and get an answer.

  const [{ count }] = await asyncQuery(`SELECT COUNT(*) as count FROM books ${interjection}`);

  const offset = (batch - 1) * size;

  if (offset >= count) {
    return [];
  }

  const query = `SELECT id, title, image, isbn FROM books ${interjection}`
      // + ` INNER JOIN book_listings ON book_listings.book_id = books.id`
      + ` LIMIT ${database.escape(size)} OFFSET ${database.escape(offset)}`;
      + ` GROUP BY books.id`

  const data = await asyncQuery(query);
  const totalBatch = Math.ceil(count / size);

  return {
    data: data,
    total: count,
    batch: batch,
    nextBatch: totalBatch === batch ? undefined : batch + 1,
    totalBatch: totalBatch,
    size: size,
  };
}

/**
 * Gets a book.
 * @param {String} bookId the id of the book you want to retrieve.
 * @returns a promise that resolves to the books or `null` is it doesn't exist.
 */
async function getBook(bookId) {
  // perform a join query on all the sub options

  const bookQuery = `SELECT * FROM books`
      // + ` LEFT JOIN book_listings ON book_listings.book_id = books.id`
      +` WHERE id = ${database.escape(bookId)} OR isbn = ${database.escape(bookId)} `;

  const [book] = await asyncQuery(bookQuery);

  if (!book) {
    return null;
  }

  const pricesQuery = `SELECT * FROM book_listings`
      + ` WHERE book_id = ${database.escape(book.id)}`

  book.prices = await asyncQuery(pricesQuery);

  const genresQuery = `SELECT genres.title as title, genres.id as id FROM books_genres`
      + ` INNER JOIN genres ON genres.id = books_genres.genre_id`
      + ` WHERE book_id = ${database.escape(book.id)}`
  book.genres = await asyncQuery(genresQuery);

  const authorsQuery = `SELECT authors.name as name, authors.id as id FROM books_authors`
      + ` INNER JOIN authors ON authors.id = books_authors.author_id`
      + ` WHERE book_id = ${database.escape(book.id)}`
  book.authors = await asyncQuery(authorsQuery);

  return book;
}

function getGenres() {
  const query =  `SELECT * FROM genres`
  return asyncQuery(query);
}

async function getBooksByGenre(id) {
  const genreQuery = `SELECT * FROM genres WHERE id = ${database.escape(id)}`;
  const [genre] = await asyncQuery(genreQuery);

  const booksQuery = `SELECT books.id as id, title, image, isbn FROM books_genres`
      + ` INNER JOIN books ON books.id = book_id`
      + ` WHERE genre_id = ${database.escape(id)}`
  genre.books = await asyncQuery(booksQuery);

  return genre;
}

function getAuthors() {
  const query =  `SELECT * FROM authors`
  return asyncQuery(query);
}

async function getBooksByAuthor(id) {
  const authorQuery = `SELECT * FROM genres WHERE id = ${database.escape(id)}`;
  const [author] = await asyncQuery(authorQuery);

  const booksQuery = `SELECT books.id as id, title, image, isbn FROM books_authors`
      + ` INNER JOIN books ON books.id = book_id`
      + ` WHERE author_id = ${database.escape(id)}`
  author.books = await asyncQuery(booksQuery);

  return author;
}

module.exports = {
  getBook,
  getBooks,
  getGenres,
  getBooksByGenre,
  getAuthors,
  getBooksByAuthor,
};
