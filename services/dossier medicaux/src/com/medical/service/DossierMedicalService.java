package com.medical.service;

import com.medical.model.DossierMedical;
import com.medical.model.HistoriqueMedical;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface DossierMedicalService {

    @WebMethod
    DossierMedical getDossierByPatientId(
        @WebParam(name = "idPatient") int idPatient
    );

    @WebMethod
    String createDossier(
        @WebParam(name = "dossier") DossierMedical dossier
    );

    @WebMethod
    String addHistorique(
        @WebParam(name = "historique") HistoriqueMedical historique
    );

    @WebMethod
    String updateHistorique(
        @WebParam(name = "historique") HistoriqueMedical historique
    );

    @WebMethod
    List<HistoriqueMedical> getHistoriqueByType(
        @WebParam(name = "idDossier") int idDossier,
        @WebParam(name = "type") String type
    );
}
