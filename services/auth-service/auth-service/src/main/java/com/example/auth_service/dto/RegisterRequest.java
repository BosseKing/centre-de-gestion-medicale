package com.example.auth_service.dto;

import com.example.auth_service.entity.User;

public class RegisterRequest {
    private String identifiant;
    private String password;
    private User.Role role;

    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}
