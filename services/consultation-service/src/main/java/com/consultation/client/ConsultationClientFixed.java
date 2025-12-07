package com.consultation.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Client SOAP Interactif utilisant des requêtes HTTP directes
 * Fonctionne sans génération de classes via wsimport
 */
public class ConsultationClientFixed {

    private static final String SERVICE_URL = "http://localhost:8086/consultation-soap/services/consultation";
    private Scanner scanner;

    public ConsultationClientFixed() {
        scanner = new Scanner(System.in);
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     CLIENT SOAP - GESTION DES CONSULTATIONS           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    // Menu principal
    public void afficherMenu() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              MENU PRINCIPAL                            ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║  1. 📝 Créer une nouvelle consultation                ║");
        System.out.println("║  2. 🔍 Rechercher une consultation par ID              ║");
        System.out.println("║  3. 📋 Afficher toutes les consultations               ║");
        System.out.println("║  4. 👤 Consultations par Patient                       ║");
        System.out.println("║  5. 👨‍⚕️ Consultations par Médecin                      ║");
        System.out.println("║  6. ✏️  Modifier une consultation                      ║");
        System.out.println("║  7. 🗑️  Supprimer une consultation                     ║");
        System.out.println("║  8. 🔌 Tester la connexion à la base                  ║");
        System.out.println("║  0. 🚪 Quitter                                         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.print("\n👉 Votre choix : ");
    }

    // Démarrer le menu interactif
    public void demarrer() {
        // Test de connexion initial
        System.out.println("\n🔄 Test de connexion au service...");
        if (!testerConnexionInitiale()) {
            System.err.println("❌ Impossible de se connecter au service SOAP");
            System.err.println("Vérifiez que Tomcat est démarré et que l'URL est correcte");
            return;
        }
        System.out.println("✅ Connexion au service réussie !\n");
        pause(1000);

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            
            try {
                int choix = scanner.nextInt();
                scanner.nextLine();
                
                switch (choix) {
                    case 1:
                        creerConsultation();
                        break;
                    case 2:
                        rechercherParId();
                        break;
                    case 3:
                        afficherToutesConsultations();
                        break;
                    case 4:
                        consultationsParPatient();
                        break;
                    case 5:
                        consultationsParMedecin();
                        break;
                    case 6:
                        modifierConsultation();
                        break;
                    case 7:
                        supprimerConsultation();
                        break;
                    case 8:
                        testerConnexion();
                        break;
                    case 0:
                        continuer = false;
                        afficherAuRevoir();
                        break;
                    default:
                        System.out.println("\n❌ Choix invalide !");
                        pause(2000);
                }
            } catch (Exception e) {
                System.err.println("\n❌ Erreur : " + e.getMessage());
                scanner.nextLine();
                pause(2000);
            }
        }
    }

    // Tester la connexion initiale
    private boolean testerConnexionInitiale() {
        try {
            URL url = new URL(SERVICE_URL + "?wsdl");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // 1. Créer une consultation
    private void creerConsultation() {
        afficherTitre("CRÉER UNE NOUVELLE CONSULTATION");
        
        try {
            System.out.print("📋 ID du Patient (ex: PAT-001) : ");
            String patientId = scanner.nextLine();
            
            System.out.print("👨‍⚕️ ID du Médecin (ex: MED-001) : ");
            String medecinId = scanner.nextLine();
            
            System.out.print("📅 Date (AAAA-MM-JJ, ex: 2025-12-15) : ");
            String date = scanner.nextLine();
            
            System.out.print("🕐 Heure (HH:MM:SS, ex: 14:30:00) : ");
            String heure = scanner.nextLine();
            
            System.out.print("📝 Motif de consultation : ");
            String motif = scanner.nextLine();
            
            System.out.print("📄 Notes (optionnel) : ");
            String notes = scanner.nextLine();
            
            System.out.println("\n⏳ Création en cours...");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:creerConsultation>" +
                "<patientId>" + patientId + "</patientId>" +
                "<medecinId>" + medecinId + "</medecinId>" +
                "<dateConsultation>" + date + "</dateConsultation>" +
                "<heureConsultation>" + heure + "</heureConsultation>" +
                "<motif>" + motif + "</motif>" +
                "<notes>" + notes + "</notes>" +
                "</ser:creerConsultation>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            if (response.contains("return")) {
                System.out.println("\n✅ Consultation créée avec succès !");
                afficherReponseXML(response);
            } else {
                System.out.println("\n❌ Erreur lors de la création");
                System.out.println(response);
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 2. Rechercher par ID
    private void rechercherParId() {
        afficherTitre("RECHERCHER UNE CONSULTATION");
        
        try {
            System.out.print("🔍 Entrez l'ID de la consultation : ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            
            System.out.println("\n⏳ Recherche en cours...");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:obtenirConsultation>" +
                "<id>" + id + "</id>" +
                "</ser:obtenirConsultation>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            if (response.contains("return")) {
                System.out.println("\n✅ Consultation trouvée !");
                afficherReponseXML(response);
            } else {
                System.out.println("\n❌ Consultation non trouvée");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 3. Afficher toutes les consultations
    private void afficherToutesConsultations() {
        afficherTitre("TOUTES LES CONSULTATIONS");
        
        try {
            System.out.println("⏳ Chargement des consultations...\n");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:obtenirToutesConsultations/>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            System.out.println("✅ Consultations récupérées !");
            afficherToutesLesConsultationsXML(response);
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 4. Consultations par patient
    private void consultationsParPatient() {
        afficherTitre("CONSULTATIONS PAR PATIENT");
        
        try {
            System.out.print("👤 ID du Patient (ex: PAT-001) : ");
            String patientId = scanner.nextLine();
            
            System.out.println("\n⏳ Recherche en cours...\n");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:obtenirConsultationsParPatient>" +
                "<patientId>" + patientId + "</patientId>" +
                "</ser:obtenirConsultationsParPatient>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            System.out.println("✅ Consultations du patient " + patientId + " :");
            afficherToutesLesConsultationsXML(response);
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 5. Consultations par médecin
    private void consultationsParMedecin() {
        afficherTitre("CONSULTATIONS PAR MÉDECIN");
        
        try {
            System.out.print("👨‍⚕️ ID du Médecin (ex: MED-001) : ");
            String medecinId = scanner.nextLine();
            
            System.out.println("\n⏳ Recherche en cours...\n");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:obtenirConsultationsParMedecin>" +
                "<medecinId>" + medecinId + "</medecinId>" +
                "</ser:obtenirConsultationsParMedecin>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            System.out.println("✅ Consultations du médecin " + medecinId + " :");
            afficherToutesLesConsultationsXML(response);
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 6. Modifier une consultation
    private void modifierConsultation() {
        afficherTitre("MODIFIER UNE CONSULTATION");
        
        try {
            System.out.print("🔍 ID de la consultation à modifier : ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            
            System.out.println("\n✏️ Nouvelles informations :");
            
            System.out.print("📋 ID Patient : ");
            String patientId = scanner.nextLine();
            
            System.out.print("👨‍⚕️ ID Médecin : ");
            String medecinId = scanner.nextLine();
            
            System.out.print("📅 Date (AAAA-MM-JJ) : ");
            String date = scanner.nextLine();
            
            System.out.print("🕐 Heure (HH:MM:SS) : ");
            String heure = scanner.nextLine();
            
            System.out.print("📝 Motif : ");
            String motif = scanner.nextLine();
            
            System.out.print("📄 Notes : ");
            String notes = scanner.nextLine();
            
            System.out.println("\n⏳ Modification en cours...");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:modifierConsultation>" +
                "<id>" + id + "</id>" +
                "<patientId>" + patientId + "</patientId>" +
                "<medecinId>" + medecinId + "</medecinId>" +
                "<dateConsultation>" + date + "</dateConsultation>" +
                "<heureConsultation>" + heure + "</heureConsultation>" +
                "<motif>" + motif + "</motif>" +
                "<notes>" + notes + "</notes>" +
                "</ser:modifierConsultation>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            System.out.println("\n✅ Consultation modifiée !");
            afficherReponseXML(response);
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 7. Supprimer une consultation
    private void supprimerConsultation() {
        afficherTitre("SUPPRIMER UNE CONSULTATION");
        
        try {
            System.out.print("🔍 ID de la consultation à supprimer : ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            
            System.out.print("\n⚠️  Confirmer la suppression ? (O/N) : ");
            String confirmation = scanner.nextLine().toUpperCase();
            
            if (confirmation.equals("O") || confirmation.equals("OUI")) {
                System.out.println("\n⏳ Suppression en cours...");
                
                String soapRequest = 
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                    "xmlns:ser=\"http://service.consultation.com/\">" +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<ser:supprimerConsultation>" +
                    "<id>" + id + "</id>" +
                    "</ser:supprimerConsultation>" +
                    "</soapenv:Body>" +
                    "</soapenv:Envelope>";
                
                String response = envoyerRequeteSOAP(soapRequest);
                
                if (response.contains("true")) {
                    System.out.println("\n✅ Consultation supprimée avec succès !");
                } else {
                    System.out.println("\n❌ Échec de la suppression");
                }
            } else {
                System.out.println("\n🚫 Suppression annulée");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // 8. Tester la connexion
    private void testerConnexion() {
        afficherTitre("TEST DE CONNEXION");
        
        try {
            System.out.println("⏳ Test en cours...\n");
            
            String soapRequest = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:ser=\"http://service.consultation.com/\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:testerConnexion/>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
            
            String response = envoyerRequeteSOAP(soapRequest);
            
            System.out.println("✅ Résultat du test :");
            afficherReponseXML(response);
            
        } catch (Exception e) {
            System.err.println("\n❌ Erreur : " + e.getMessage());
        }
        
        attendreEntree();
    }

    // Envoyer une requête SOAP
    private String envoyerRequeteSOAP(String soapRequest) throws Exception {
        URL url = new URL(SERVICE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapRequest.getBytes("UTF-8"));
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        conn.disconnect();
        return response.toString();
    }

    // Afficher la réponse XML formatée
    private void afficherReponseXML(String xml) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        
        // Extraction simple des valeurs
        String[] balises = {"id>", "patientId>", "medecinId>", "dateConsultation>", 
                           "heureConsultation>", "motif>", "notes>"};
        String[] labels = {"🆔 ID", "👤 Patient", "👨‍⚕️ Médecin", "📅 Date", 
                          "🕐 Heure", "📝 Motif", "📄 Notes"};
        
        for (int i = 0; i < balises.length; i++) {
            String valeur = extraireValeur(xml, balises[i]);
            if (valeur != null && !valeur.isEmpty()) {
                System.out.println("│ " + labels[i] + " : " + valeur);
            }
        }
        
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    // Afficher toutes les consultations (pour les listes)
    private void afficherToutesLesConsultationsXML(String xml) {
        // Compter le nombre de consultations
        int count = 0;
        int pos = 0;
        while ((pos = xml.indexOf("<return>", pos)) != -1) {
            count++;
            pos++;
        }
        
        if (count == 0) {
            System.out.println("\nℹ️  Aucune consultation trouvée.");
            return;
        }
        
        System.out.println("\n📊 Nombre total : " + count + " consultation(s)\n");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        // Extraire et afficher chaque consultation
        int startPos = 0;
        int consultationNum = 1;
        
        while ((startPos = xml.indexOf("<return>", startPos)) != -1) {
            int endPos = xml.indexOf("</return>", startPos);
            if (endPos == -1) break;
            
            String consultationXml = xml.substring(startPos, endPos + 9);
            
            System.out.println("\n🔹 Consultation #" + consultationNum);
            System.out.println("┌─────────────────────────────────────────────────────────┐");
            
            String[] balises = {"id>", "patientId>", "medecinId>", "dateConsultation>", 
                               "heureConsultation>", "motif>", "notes>"};
            String[] labels = {"🆔 ID", "👤 Patient", "👨‍⚕️ Médecin", "📅 Date", 
                              "🕐 Heure", "📝 Motif", "📄 Notes"};
            
            for (int i = 0; i < balises.length; i++) {
                String valeur = extraireValeur(consultationXml, balises[i]);
                if (valeur != null && !valeur.isEmpty()) {
                    System.out.println("│ " + labels[i] + " : " + valeur);
                }
            }
            
            System.out.println("└─────────────────────────────────────────────────────────┘");
            
            consultationNum++;
            startPos = endPos + 1;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
    }

    // Extraire une valeur d'une balise XML
    private String extraireValeur(String xml, String balise) {
        try {
            int debut = xml.indexOf("<" + balise) + balise.length() + 1;
            int fin = xml.indexOf("</" + balise);
            if (debut > 0 && fin > debut) {
                return xml.substring(debut, fin).trim();
            }
        } catch (Exception e) {
            // Ignorer
        }
        return "";
    }

    // Utilitaires
    private void afficherTitre(String titre) {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  " + centrer(titre, 52) + "  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    private String centrer(String texte, int largeur) {
        int padding = (largeur - texte.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) sb.append(" ");
        sb.append(texte);
        while (sb.length() < largeur) sb.append(" ");
        return sb.toString();
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private void attendreEntree() {
        System.out.print("\n📌 Appuyez sur ENTRÉE pour continuer...");
        scanner.nextLine();
    }

    private void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void afficherAuRevoir() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    AU REVOIR ! 👋                      ║");
        System.out.println("║                                                        ║");
        System.out.println("║       Merci d'avoir utilisé le client SOAP             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    // Main
    public static void main(String[] args) {
        ConsultationClientFixed client = new ConsultationClientFixed();
        client.demarrer();
    }
}