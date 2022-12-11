const database = require("./database");

/**
 * Gets all cameras in the database.
 * @param {Number} batch where to start from
 * @param {Number} size number of results to return.
 * @param {String} search number
 * @returns a promise that resolves to null if nothing was found or an array of results.
 */
function getBooks(batch, size, search) {
  if (typeof batch !== "number" || typeof size !== "number") {
    throw new TypeError("Expected 'start' and 'step' to be of type 'number'.");
  }

  // TODO: How to tell the count of a query while only return a few segment from it?
  // TODO: How to join both request and get an answer.

  // join this query to get the book_listing.
  const queryStringBeginning = `SELECT title, image, isbn, MIN(price) as best_price, COUNT(provider) as provider_count FROM books `
  const queryStringEnding = `LIMIT $0 OFFSET $1`;
  const queryString = queryStringBeginning + queryStringEnding;

  return new Promise(function (resolve, reject) {
    database.query(queryString, [size, batch * size], async function(error, result, fields) {
      if (error) {
        reject(error);
      }

      if (!Array.isArray(result)) {
        reject("Expected 'result' to be an 'array' but received " + typeof result);
      }

      return result;
    })
  })
}

/**
 * Gets the cheapest listing available.
 * @param {String} bookId the id of the camera you want to retrieve the cheapest price.
 * @returns a promise that resolves to the cheapest listing if it is in the database.
 */
function getBook(bookId) {
  // perform a join query on all the sub options
  const queryString = `SELECT * FROM listings WHERE id = ${database.escape(bookId)} OR isbn = ${database.escape(bookId)} `;

  return new Promise(function (resolve, reject) {

    database.query(queryString,async function (error, result) {
      if (error) reject(error);

      let cheapestListing = null;

      for (const listing of result) {
        if (!cheapestListing || listing.price < cheapestListing.price)
          cheapestListing = listing;
      }

      resolve(cheapestListing);
    });
  });
}

function getGenres() {
  return Promise.resolve([]);
}

function getBooksByGenre() {
  return Promise.resolve([]);
}

function getAuthors() {
  return Promise.resolve([]);
}

function getBooksByAuthor() {
  return Promise.resolve([]);
}


module.exports = {
  getBook,
  getBooks,
  getGenres,
  getBooksByGenre,
  getAuthors,
  getBooksByAuthor,
};
