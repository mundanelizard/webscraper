var mysql = require("mysql");

var connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "pterodactyl",
  database: "webscraper",
});

connection.connect();

process.on("beforeExit", () => {
  console.log("Terminating database...")
  connection.end();
  console.log("Successfully terminated database");
})

module.exports = connection;
