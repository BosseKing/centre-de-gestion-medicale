package com.medical.emailservice.dto;

import jakarta.validation.constraints.NotBlank;

public class AttachmentDto {
    
    @NotBlank
    private String filename;
    
    @NotBlank
    private String contentType;
    
    private byte[] content;
    
    private String base64Content;
    
    // Constructeurs
    public AttachmentDto() {
    }
    
    public AttachmentDto(String filename, String contentType, byte[] content, String base64Content) {
        this.filename = filename;
        this.contentType = contentType;
        this.content = content;
        this.base64Content = base64Content;
    }
    
    // Getters et Setters
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public byte[] getContent() {
        return content;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }
    
    public String getBase64Content() {
        return base64Content;
    }
    
    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }
}