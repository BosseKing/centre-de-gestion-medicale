package com.example.auth_service.service;

import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByIdentifiant(request.getIdentifiant())) {
            return "Identifiant déjà utilisé!";
        }

        User user = new User();
        user.setIdentifiant(request.getIdentifiant());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
        return "Utilisateur créé avec succès!";
    }

    public String login(String identifiant, String password) {
        var optionalUser = userRepository.findByIdentifiant(identifiant);
        if (optionalUser.isEmpty()) return "Utilisateur non trouvé!";

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Mot de passe incorrect!";
        }

        return "Connexion réussie!";
    }
}
