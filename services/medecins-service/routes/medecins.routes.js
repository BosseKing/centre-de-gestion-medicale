const express = require("express");
const router = express.Router();
const db = require("../db");

/**
 * ➕ Ajouter un médecin
 */
router.post("/", (req, res) => {
    const {
        reference_medecin,
        num_cin,
        nom,
        prenom,
        telephone,
        email,
        specialite,
        jours_travail
    } = req.body;

    const sql = `
        INSERT INTO medecins
        (reference_medecin, num_cin, nom, prenom, telephone, email, specialite, jours_travail)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;

    db.query(sql, [
        reference_medecin,
        num_cin,
        nom,
        prenom,
        telephone,
        email,
        specialite,
        jours_travail
    ], (err, result) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.json({ message: "Médecin ajouté avec succès", id: result.insertId });
    });
});

/**
 * 📋 Lister tous les médecins
 */
router.get("/", (req, res) => {
    db.query("SELECT * FROM medecins", (err, results) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

/**
 * 🔍 Rechercher médecin par référence
 */
router.get("/:reference", (req, res) => {
    db.query(
        "SELECT * FROM medecins WHERE reference_medecin = ?",
        [req.params.reference],
        (err, results) => {
            if (err) {
                return res.status(500).json({ error: err.message });
            }
            res.json(results[0] || {});
        }
    );
});


/**
 * ❌ Supprimer médecin par Référence
 */
router.delete("/reference/:reference_medecin", (req, res) => {
    db.query(
        "DELETE FROM medecins WHERE reference_medecin = ?",
        [req.params.reference_medecin],
        (err, result) => {
            if (err) {
                return res.status(500).json({ error: err.message });
            }

            if (result.affectedRows === 0) {
                return res.status(404).json({
                    message: "Médecin non trouvé avec cette référence"
                });
            }

            res.json({
                message: "Médecin supprimé avec succès"
            });
        }
    );
});


module.exports = router;
