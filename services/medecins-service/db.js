const mysql = require("mysql2");

const db = mysql.createConnection({
    host: "127.0.0.1",
    user: "root",
    password: "",
    database: "medecins"
});

db.connect(err => {
    if (err) {
        console.error("Erreur connexion MySQL :", err);
    } else {
        console.log("Connecté à MySQL");
    }
});

module.exports = db;
