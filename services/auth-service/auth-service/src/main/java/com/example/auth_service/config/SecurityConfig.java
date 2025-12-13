package com.example.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // désactive CSRF pour Postman ou frontend
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // toutes les requêtes sont autorisées
            )
            .httpBasic(httpBasic -> httpBasic.disable()); // désactive HTTP Basic
        return http.build();
    }
}
