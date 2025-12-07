package com.consultation.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class Consultation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String patientId;
    private String medecinId;
    private Date dateConsultation;
    private Time heureConsultation;
    private String motif;
    private String notes;

    // Constructeurs
    public Consultation() {
    }

    public Consultation(String patientId, String medecinId, Date dateConsultation, 
                       Time heureConsultation, String motif, String notes) {
        this.patientId = patientId;
        this.medecinId = medecinId;
        this.dateConsultation = dateConsultation;
        this.heureConsultation = heureConsultation;
        this.motif = motif;
        this.notes = notes;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(String medecinId) {
        this.medecinId = medecinId;
    }

    public Date getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(Date dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public Time getHeureConsultation() {
        return heureConsultation;
    }

    public void setHeureConsultation(Time heureConsultation) {
        this.heureConsultation = heureConsultation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", patientId='" + patientId + '\'' +
                ", medecinId='" + medecinId + '\'' +
                ", dateConsultation=" + dateConsultation +
                ", heureConsultation=" + heureConsultation +
                ", motif='" + motif + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}