const database = require("../database");

/**
 * Gets all cameras in the database.
 * @param {Number} start where to start from
 * @param {Number} step number of results to return.
 * @returns a promise that resolves to null if nothing was found or an array of results.
 */
function getCameras(start, step) {
  if (typeof start !== "number" && typeof step !== "number") {
    throw new TypeError("Expected 'start' and 'step' to be of type 'number'.");
  }

  const queryString = `SELECT * FROM cameras LIMIT ${step} OFFSET ${start}`;

  return new Promise(function (resolve, reject) {
    database.query(queryString, async function (error, result, fields) {
      if (error) reject(error);

      // batching promises together
      const listingsPromises = result.map(async (camera) =>
        await getCheapestListing(camera.camera_id)
      );

      // resolving promises at once to increase efficiency
      const listings = await Promise.all(listingsPromises);

      const newResult = result.map((camera) => {        
        const listing = listings.find(
          (listing) => camera.camera_id === listing.camera_id
        );
        
        return { ...camera, ...listing };
      });

      resolve(newResult);
    });
  });
}

/**
 * Gets the cheapest listing available.
 * @param {Number} cameraId the id of the camera you want to retrieve the cheapest price.
 * @returns a promise that resolves to the cheapest listing if it is in the database.
 */
function getCheapestListing(cameraId) {
  if (typeof cameraId !== "number") {
    throw new TypeError("Expected 'cameraId' to be of type 'number'");
  }

  const queryString = `SELECT * FROM listings WHERE camera_id = ${cameraId}`;

  return new Promise(function (resolve, reject) {
    database.query(queryString, async function (error, result, fields) {
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

/**
 * Gets a cemra from the database.
 * @param {Number} camerId id of the camera to be found.
 * @returns returns a camera if one exists and null if it doesn't.
 */
function getCamera(cameraId) {
  // does some weird magic

  const queryStrings = [`SELECT * FROM cameras WHERE camera_id = ${cameraId}`, `SELECT * FROM listings WHERE camera_id = ${cameraId}`];

  return new Promise(function(resolve, reject) {
    database.query(queryStrings[0], async function(error, result, fields) {
      if (error) reject(error);
      const details = result[0];
      database.query(queryStrings[1], async function(error, listings, fields) {
        if (error) reject(error);

        const response = {
          details,
          listings,
        }

        resolve(response);
      });
    });
  });
}

/**
 * Searches for cameras that matches the 'search'.
 * @param {String} search name of the camera .
 * @returns a promise that resolves to an array of cameras.
 */
function searchCameras(start, step, search) {
  if (typeof start !== "number" && typeof step !== "number") {
    throw new TypeError("Expected 'start' and 'step' to be of type 'number'.");
  }

  const queryString = `SELECT * FROM cameras WHERE title LIKE '%${search}%' LIMIT ${step} OFFSET ${start}`;

  return new Promise(function (resolve, reject) {
    database.query(queryString, async function (error, result, fields) {
      if (error) reject(error);

      // batching promises together
      const listingsPromises = result.map(
        async (camera) => await getCheapestListing(camera.camera_id)
      );

      // resolving promises at once to increase efficiency
      const listings = await Promise.all(listingsPromises);

      const newResult = result.map((camera) => {
        const listing = listings.find(
          (listing) => camera.camera_id === listing.camera_id
        );
        return { ...camera, ...listing };
      });

      resolve(newResult);
    });
  });
}

module.exports = {
  getCamera,
  getCameras,
  searchCameras,
};
