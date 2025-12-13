package com.medical.emailservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public class EmailRequestDto {
    
    @NotEmpty(message = "Au moins un destinataire requis")
    private List<@Email(message = "Format email invalide") String> to;
    
    private List<@Email String> cc;
    
    private List<@Email String> bcc;
    
    @NotBlank(message = "Le sujet est obligatoire")
    private String subject;
    
    @NotBlank(message = "Le corps du message est obligatoire")
    private String body;
    
    private boolean isHtml = false;
    
    private List<AttachmentDto> attachments;
    
    private String templateName;
    
    private Map<String, Object> templateVariables;
    
    private EmailPriority priority = EmailPriority.NORMAL;
    
    // Constructeurs
    public EmailRequestDto() {
    }
    
    public EmailRequestDto(List<String> to, List<String> cc, List<String> bcc, String subject, 
                          String body, boolean isHtml, List<AttachmentDto> attachments, 
                          String templateName, Map<String, Object> templateVariables, 
                          EmailPriority priority) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
        this.attachments = attachments;
        this.templateName = templateName;
        this.templateVariables = templateVariables;
        this.priority = priority;
    }
    
    // Getters et Setters
    public List<String> getTo() {
        return to;
    }
    
    public void setTo(List<String> to) {
        this.to = to;
    }
    
    public List<String> getCc() {
        return cc;
    }
    
    public void setCc(List<String> cc) {
        this.cc = cc;
    }
    
    public List<String> getBcc() {
        return bcc;
    }
    
    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public boolean isHtml() {
        return isHtml;
    }
    
    public void setHtml(boolean html) {
        isHtml = html;
    }
    
    public List<AttachmentDto> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }
    
    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }
    
    public EmailPriority getPriority() {
        return priority;
    }
    
    public void setPriority(EmailPriority priority) {
        this.priority = priority;
    }
}