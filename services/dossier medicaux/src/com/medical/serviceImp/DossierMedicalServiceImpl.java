package com.medical.serviceImp;

import com.medical.model.DossierMedical;
import com.medical.model.HistoriqueMedical;
import com.medical.service.DossierMedicalService;
import com.medical.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;

@WebService(endpointInterface = "com.medical.service.DossierMedicalService")
public class DossierMedicalServiceImpl implements DossierMedicalService {

    @Override
    public DossierMedical getDossierByPatientId(int idPatient) {
        DossierMedical dossier = null;
        String sql = "SELECT * FROM dossier_medical WHERE id_patient = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPatient);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dossier = new DossierMedical();
                dossier.setIdDossier(rs.getInt("id_dossier"));
                dossier.setIdPatient(rs.getInt("id_patient"));
                dossier.setNom(rs.getString("nom"));
                dossier.setPrenom(rs.getString("prenom"));
                dossier.setDateNaissance(rs.getString("date_naissance")); // <-- String
                dossier.setSexe(rs.getString("sexe"));
                dossier.setGroupeSanguin(rs.getString("groupe_sanguin"));
                dossier.setNumeroCin(rs.getString("numero_cin"));
                dossier.setNumeroTelephone(rs.getString("numero_telephone"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dossier;
    }

    @Override
    public String createDossier(DossierMedical dossier) {
        String sql = "INSERT INTO dossier_medical (id_patient, nom, prenom, date_naissance, sexe, groupe_sanguin, numero_cin, numero_telephone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dossier.getIdPatient());
            stmt.setString(2, dossier.getNom());
            stmt.setString(3, dossier.getPrenom());
            stmt.setString(4, dossier.getDateNaissance()); // <-- String
            stmt.setString(5, dossier.getSexe());
            stmt.setString(6, dossier.getGroupeSanguin());
            stmt.setString(7, dossier.getNumeroCin());
            stmt.setString(8, dossier.getNumeroTelephone());

            int rows = stmt.executeUpdate();
            if (rows > 0) return "Dossier medical cree avec succes";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors de la creation du dossier";
    }

    @Override
    public String addHistorique(HistoriqueMedical historique) {
        String sql = "INSERT INTO historique_medical (id_dossier, type, date_evenement, traitement, titre) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, historique.getIdDossier());
            stmt.setString(2, historique.getType());
            stmt.setString(3, historique.getDateEvenement()); // <-- String
            stmt.setString(4, historique.getTraitement());
            stmt.setString(5, historique.getTitre());

            int rows = stmt.executeUpdate();
            if (rows > 0) return "Historique ajoute avec succes";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors de l'ajout de l'historique";
    }

    @Override
    public String updateHistorique(HistoriqueMedical historique) {
        String sql = "UPDATE historique_medical SET type = ?, date_evenement = ?, traitement = ?, titre = ? WHERE id_historique = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, historique.getType());
            stmt.setString(2, historique.getDateEvenement()); // <-- String
            stmt.setString(3, historique.getTraitement());
            stmt.setString(4, historique.getTitre());
            stmt.setInt(5, historique.getIdHistorique());

            int rows = stmt.executeUpdate();
            if (rows > 0) return "Historique mis ajour avec succes";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors de la mise a jour de l'historique";
    }

    @Override
    public List<HistoriqueMedical> getHistoriqueByType(int idDossier, String type) {
        List<HistoriqueMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM historique_medical WHERE id_dossier = ? AND type = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDossier);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistoriqueMedical h = new HistoriqueMedical();
                h.setIdHistorique(rs.getInt("id_historique"));
                h.setIdDossier(rs.getInt("id_dossier"));
                h.setType(rs.getString("type"));
                h.setDateEvenement(rs.getString("date_evenement")); // <-- String
                h.setTraitement(rs.getString("traitement"));
                h.setTitre(rs.getString("titre"));
                list.add(h);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
