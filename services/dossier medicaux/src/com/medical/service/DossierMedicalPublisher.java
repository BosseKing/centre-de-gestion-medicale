package com.medical.service;

import com.medical.serviceImp.DossierMedicalServiceImpl;
import javax.xml.ws.Endpoint;

public class DossierMedicalPublisher {

    public static void main(String[] args) {
        Endpoint.publish(
            "http://localhost:8085/dossierMedical",new DossierMedicalServiceImpl());

        System.out.println(
            "Service SOAP Dossier Medical demarre");
    }
}
