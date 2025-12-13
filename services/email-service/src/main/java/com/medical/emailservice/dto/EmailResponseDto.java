package com.medical.emailservice.dto;

import java.time.LocalDateTime;

public class EmailResponseDto {
    
    private boolean success;
    private String message;
    private String emailId;
    private LocalDateTime sentAt;
    private int recipientCount;
    
    // Constructeurs
    public EmailResponseDto() {
    }
    
    public EmailResponseDto(boolean success, String message, String emailId, 
                           LocalDateTime sentAt, int recipientCount) {
        this.success = success;
        this.message = message;
        this.emailId = emailId;
        this.sentAt = sentAt;
        this.recipientCount = recipientCount;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private boolean success;
        private String message;
        private String emailId;
        private LocalDateTime sentAt;
        private int recipientCount;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder emailId(String emailId) {
            this.emailId = emailId;
            return this;
        }
        
        public Builder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }
        
        public Builder recipientCount(int recipientCount) {
            this.recipientCount = recipientCount;
            return this;
        }
        
        public EmailResponseDto build() {
            return new EmailResponseDto(success, message, emailId, sentAt, recipientCount);
        }
    }
    
    // Getters et Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getEmailId() {
        return emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public int getRecipientCount() {
        return recipientCount;
    }
    
    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }
}