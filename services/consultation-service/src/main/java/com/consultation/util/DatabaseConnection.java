package com.consultation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // Chaîne de connexion mise à jour selon ta configuration
    private static final String URL = "jdbc:mysql://localhost:3306/consultation_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "96931275";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Chargement du driver MySQL
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("Driver MySQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur de chargement du driver MySQL: " + e.getMessage());
            throw new RuntimeException("Driver MySQL introuvable", e);
        }
    }

    // Méthode pour obtenir une connexion valide
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion MySQL établie avec succès ✔");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur de connexion MySQL ❌ : " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour fermer la connexion proprement
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connexion MySQL fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }

    // Test de connexion (utile pour vérifier ton installation)
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
}
