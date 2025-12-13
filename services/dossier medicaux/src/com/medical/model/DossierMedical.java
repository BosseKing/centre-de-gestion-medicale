package com.medical.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DossierMedical {

    private int idDossier;
    private int idPatient;
    private String nom;
    private String prenom;
    private String dateNaissance; // <-- String au lieu de java.sql.Date
    private String sexe;
    private String groupeSanguin;
    private String numeroCin;
    private String numeroTelephone;

    // Getters & Setters
    public int getIdDossier() { return idDossier; }
    public void setIdDossier(int idDossier) { this.idDossier = idDossier; }

    public int getIdPatient() { return idPatient; }
    public void setIdPatient(int idPatient) { this.idPatient = idPatient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public String getNumeroCin() { return numeroCin; }
    public void setNumeroCin(String numeroCin) { this.numeroCin = numeroCin; }

    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
}
