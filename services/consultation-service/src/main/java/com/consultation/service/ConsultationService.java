package com.consultation.service;

import com.consultation.dao.ConsultationDAO;
import com.consultation.model.Consultation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

@WebService(serviceName = "ConsultationService")
public class ConsultationService {

    private ConsultationDAO consultationDAO = new ConsultationDAO();

    @WebMethod(operationName = "creerConsultation")
    public Consultation creerConsultation(
            @WebParam(name = "patientId") String patientId,
            @WebParam(name = "medecinId") String medecinId,
            @WebParam(name = "dateConsultation") String dateConsultation,
            @WebParam(name = "heureConsultation") String heureConsultation,
            @WebParam(name = "motif") String motif,
            @WebParam(name = "notes") String notes) {
        
        try {
            Consultation consultation = new Consultation();
            consultation.setPatientId(patientId);
            consultation.setMedecinId(medecinId);
            consultation.setDateConsultation(Date.valueOf(dateConsultation));
            consultation.setHeureConsultation(Time.valueOf(heureConsultation));
            consultation.setMotif(motif);
            consultation.setNotes(notes);
            
            return consultationDAO.create(consultation);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la consultation", e);
        }
    }

    @WebMethod(operationName = "obtenirConsultation")
    public Consultation obtenirConsultation(@WebParam(name = "id") Long id) {
        try {
            Consultation consultation = consultationDAO.findById(id);
            if (consultation == null) {
                throw new RuntimeException("Consultation introuvable avec l'ID: " + id);
            }
            return consultation;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de la consultation", e);
        }
    }

    @WebMethod(operationName = "obtenirToutesConsultations")
    public List<Consultation> obtenirToutesConsultations() {
        try {
            return consultationDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des consultations", e);
        }
    }

    @WebMethod(operationName = "obtenirConsultationsParPatient")
    public List<Consultation> obtenirConsultationsParPatient(
            @WebParam(name = "patientId") String patientId) {
        try {
            return consultationDAO.findByPatientId(patientId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des consultations du patient", e);
        }
    }

    @WebMethod(operationName = "obtenirConsultationsParMedecin")
    public List<Consultation> obtenirConsultationsParMedecin(
            @WebParam(name = "medecinId") String medecinId) {
        try {
            return consultationDAO.findByMedecinId(medecinId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des consultations du médecin", e);
        }
    }

    @WebMethod(operationName = "modifierConsultation")
    public Consultation modifierConsultation(
            @WebParam(name = "id") Long id,
            @WebParam(name = "patientId") String patientId,
            @WebParam(name = "medecinId") String medecinId,
            @WebParam(name = "dateConsultation") String dateConsultation,
            @WebParam(name = "heureConsultation") String heureConsultation,
            @WebParam(name = "motif") String motif,
            @WebParam(name = "notes") String notes) {
        
        try {
            Consultation consultation = consultationDAO.findById(id);
            if (consultation == null) {
                throw new RuntimeException("Consultation introuvable avec l'ID: " + id);
            }
            
            consultation.setPatientId(patientId);
            consultation.setMedecinId(medecinId);
            consultation.setDateConsultation(Date.valueOf(dateConsultation));
            consultation.setHeureConsultation(Time.valueOf(heureConsultation));
            consultation.setMotif(motif);
            consultation.setNotes(notes);
            
            return consultationDAO.update(consultation);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de la consultation", e);
        }
    }

    @WebMethod(operationName = "supprimerConsultation")
    public boolean supprimerConsultation(@WebParam(name = "id") Long id) {
        try {
            boolean deleted = consultationDAO.delete(id);
            if (!deleted) {
                throw new RuntimeException("Consultation introuvable avec l'ID: " + id);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de la consultation", e);
        }
    }

    @WebMethod(operationName = "testerConnexion")
    public String testerConnexion() {
        try {
            consultationDAO.findAll();
            return "Connexion réussie à la base de données !";
        } catch (Exception e) {
            return "Erreur de connexion: " + e.getMessage();
        }
    }
}