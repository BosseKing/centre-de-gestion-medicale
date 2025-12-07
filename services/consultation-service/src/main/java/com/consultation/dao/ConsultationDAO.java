package com.consultation.dao;

import com.consultation.model.Consultation;
import com.consultation.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {

    // Créer une consultation
    public Consultation create(Consultation consultation) throws SQLException {
        String sql = "INSERT INTO consultation (patient_id, medecin_id, date_consultation, " +
                    "heure_consultation, motif, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, consultation.getPatientId());
            stmt.setString(2, consultation.getMedecinId());
            stmt.setDate(3, consultation.getDateConsultation());
            stmt.setTime(4, consultation.getHeureConsultation());
            stmt.setString(5, consultation.getMotif());
            stmt.setString(6, consultation.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        consultation.setId(generatedKeys.getLong(1));
                    }
                }
            }
            
            return consultation;
        }
    }

    // Récupérer une consultation par ID
    public Consultation findById(Long id) throws SQLException {
        String sql = "SELECT * FROM consultation WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConsultation(rs);
                }
            }
        }
        return null;
    }

    // Récupérer toutes les consultations
    public List<Consultation> findAll() throws SQLException {
        String sql = "SELECT * FROM consultation ORDER BY date_consultation DESC, heure_consultation DESC";
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                consultations.add(mapResultSetToConsultation(rs));
            }
        }
        
        return consultations;
    }

    // Récupérer les consultations par patient
    public List<Consultation> findByPatientId(String patientId) throws SQLException {
        String sql = "SELECT * FROM consultation WHERE patient_id = ? " +
                    "ORDER BY date_consultation DESC, heure_consultation DESC";
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultation(rs));
                }
            }
        }
        
        return consultations;
    }

    // Récupérer les consultations par médecin
    public List<Consultation> findByMedecinId(String medecinId) throws SQLException {
        String sql = "SELECT * FROM consultation WHERE medecin_id = ? " +
                    "ORDER BY date_consultation DESC, heure_consultation DESC";
        List<Consultation> consultations = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medecinId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultations.add(mapResultSetToConsultation(rs));
                }
            }
        }
        
        return consultations;
    }

    // Mettre à jour une consultation
    public Consultation update(Consultation consultation) throws SQLException {
        String sql = "UPDATE consultation SET patient_id = ?, medecin_id = ?, " +
                    "date_consultation = ?, heure_consultation = ?, motif = ?, notes = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, consultation.getPatientId());
            stmt.setString(2, consultation.getMedecinId());
            stmt.setDate(3, consultation.getDateConsultation());
            stmt.setTime(4, consultation.getHeureConsultation());
            stmt.setString(5, consultation.getMotif());
            stmt.setString(6, consultation.getNotes());
            stmt.setLong(7, consultation.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                return consultation;
            }
        }
        
        return null;
    }

    // Supprimer une consultation
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM consultation WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Mapper ResultSet vers Consultation
    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setId(rs.getLong("id"));
        consultation.setPatientId(rs.getString("patient_id"));
        consultation.setMedecinId(rs.getString("medecin_id"));
        consultation.setDateConsultation(rs.getDate("date_consultation"));
        consultation.setHeureConsultation(rs.getTime("heure_consultation"));
        consultation.setMotif(rs.getString("motif"));
        consultation.setNotes(rs.getString("notes"));
        return consultation;
    }
}