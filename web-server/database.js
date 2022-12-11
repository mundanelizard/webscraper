const mysql = require("mysql");

const connection = mysql.createConnection({
  host: "127.0.0.1",
  user: "root",
  password: "password",
  database: "webscraper",
});

connection.connect();

process.on("beforeExit", () => {
  console.log("Terminating database...")
  connection.end();
  console.log("Successfully terminated database");
})

module.exports = connection;
