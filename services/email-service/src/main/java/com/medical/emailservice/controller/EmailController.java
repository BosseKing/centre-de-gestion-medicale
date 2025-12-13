package com.medical.emailservice.controller;

import com.medical.emailservice.dto.EmailRequestDto;
import com.medical.emailservice.dto.EmailResponseDto;
import com.medical.emailservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class EmailController {
    
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @PostMapping("/send")
    public ResponseEntity<EmailResponseDto> sendEmail(@Valid @RequestBody EmailRequestDto request) {
        log.info("=== RÉCEPTION REQUÊTE D'ENVOI EMAIL ===");
        log.info("Destinataires: {}", request.getTo());
        log.info("Sujet: {}", request.getSubject());
        log.info("isHtml: {}", request.isHtml());
        log.info("Template: {}", request.getTemplateName());
        
        EmailResponseDto response = emailService.sendEmail(request);
        
        log.info("=== RÉPONSE ENVOYÉE ===");
        log.info("Success: {}", response.isSuccess());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Health check appelé");
        return ResponseEntity.ok("Email Service is running");
    }
    
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        log.info("Vérification de la configuration...");
        
        Map<String, Object> config = new HashMap<>();
        config.put("status", "OK");
        config.put("smtp_host", mailHost);
        config.put("smtp_port", mailPort);
        config.put("smtp_username", mailUsername);
        config.put("password_configured", mailUsername != null && !mailUsername.isEmpty());
        
        try {
            MimeMessage testMessage = mailSender.createMimeMessage();
            config.put("mailsender_status", "OK - JavaMailSender créé avec succès");
        } catch (Exception e) {
            config.put("mailsender_status", "ERREUR: " + e.getMessage());
            config.put("error", true);
        }
        
        log.info("Configuration: {}", config);
        return ResponseEntity.ok(config);
    }
    
    @GetMapping("/test-smtp")
    public ResponseEntity<Map<String, String>> testSmtp() {
        Map<String, String> result = new HashMap<>();
        
        try {
            log.info("Test de connexion SMTP...");
            MimeMessage message = mailSender.createMimeMessage();
            
            result.put("status", "SUCCESS");
            result.put("message", "Configuration SMTP valide - Prêt à envoyer des emails");
            result.put("smtp_host", mailHost);
            result.put("smtp_port", String.valueOf(mailPort));
            result.put("smtp_user", mailUsername);
            
            log.info("Test SMTP réussi");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Test SMTP échoué", e);
            result.put("status", "ERROR");
            result.put("message", "Erreur de configuration SMTP");
            result.put("error", e.getMessage());
            result.put("error_type", e.getClass().getName());
            
            return ResponseEntity.status(500).body(result);
        }
    }
}