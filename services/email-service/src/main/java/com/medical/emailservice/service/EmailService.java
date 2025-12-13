package com.medical.emailservice.service;

import com.medical.emailservice.dto.AttachmentDto;
import com.medical.emailservice.dto.EmailPriority;
import com.medical.emailservice.dto.EmailRequestDto;
import com.medical.emailservice.dto.EmailResponseDto;
import com.medical.emailservice.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${email.service.default-from-name:Medical System}")
    private String defaultFromName;
    
    public EmailResponseDto sendEmail(EmailRequestDto request) {
        log.info("=== DÉBUT ENVOI EMAIL ===");
        log.info("Destinataires: {}", request.getTo());
        log.info("Sujet: {}", request.getSubject());
        log.info("Template: {}", request.getTemplateName());
        
        try {
            log.info("Création du message MIME...");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            log.info("Configuration de l'expéditeur...");
            helper.setFrom(fromEmail);
            
            log.info("Configuration des destinataires...");
            helper.setTo(request.getTo().toArray(new String[0]));
            
            log.info("Configuration du sujet...");
            helper.setSubject(request.getSubject());
            
            if (request.getCc() != null && !request.getCc().isEmpty()) {
                log.info("Ajout CC...");
                helper.setCc(request.getCc().toArray(new String[0]));
            }
            
            if (request.getBcc() != null && !request.getBcc().isEmpty()) {
                log.info("Ajout BCC...");
                helper.setBcc(request.getBcc().toArray(new String[0]));
            }
            
            log.info("Génération du corps du message...");
            String body = getEmailBody(request);
            log.info("Longueur du corps: {} caractères", body.length());
            
            helper.setText(body, request.isHtml());
            
            log.info("Configuration de la priorité...");
            setPriority(message, request.getPriority());
            
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                log.info("Ajout de {} pièces jointes...", request.getAttachments().size());
                addAttachments(helper, request.getAttachments());
            }
            
            log.info("Envoi du message...");
            mailSender.send(message);
            
            String emailId = UUID.randomUUID().toString();
            log.info("=== EMAIL ENVOYÉ AVEC SUCCÈS - ID: {} ===", emailId);
            
            return EmailResponseDto.builder()
                    .success(true)
                    .message("Email envoyé avec succès")
                    .emailId(emailId)
                    .sentAt(LocalDateTime.now())
                    .recipientCount(request.getTo().size())
                    .build();
                    
        } catch (Exception e) {
            log.error("=== ERREUR LORS DE L'ENVOI ===");
            log.error("Type d'erreur: {}", e.getClass().getName());
            log.error("Message d'erreur: {}", e.getMessage());
            log.error("Stack trace:", e);
            throw new EmailSendException("Échec de l'envoi d'email: " + e.getMessage(), e);
        }
    }
    
    private String getEmailBody(EmailRequestDto request) {
        if (request.getTemplateName() != null && !request.getTemplateName().isEmpty()) {
            return processTemplate(request.getTemplateName(), request.getTemplateVariables());
        }
        return request.getBody();
    }
    
    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return templateEngine.process(templateName, context);
    }
    
    private void setPriority(MimeMessage message, EmailPriority priority) throws MessagingException {
        switch (priority) {
            case HIGH:
                message.setHeader("X-Priority", "2");
                message.setHeader("Importance", "High");
                break;
            case URGENT:
                message.setHeader("X-Priority", "1");
                message.setHeader("Importance", "High");
                break;
            case LOW:
                message.setHeader("X-Priority", "4");
                message.setHeader("Importance", "Low");
                break;
            default:
                message.setHeader("X-Priority", "3");
                break;
        }
    }
    
    private void addAttachments(MimeMessageHelper helper, List<AttachmentDto> attachments) 
            throws MessagingException {
        for (AttachmentDto attachment : attachments) {
            byte[] content = attachment.getContent();
            if (content == null && attachment.getBase64Content() != null) {
                content = Base64.getDecoder().decode(attachment.getBase64Content());
            }
            
            if (content != null) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(
                    content, attachment.getContentType());
                helper.addAttachment(attachment.getFilename(), dataSource);
            }
        }
    }
}
