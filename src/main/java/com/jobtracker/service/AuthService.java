package com.jobtracker.service;

import com.jobtracker.dto.Dtos.*;
import com.jobtracker.model.User;
import com.jobtracker.repository.UserRepository;
import com.jobtracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest r) {
        if (userRepository.existsByEmail(r.email())) throw new IllegalArgumentException("Email already registered");
        User u = new User();
        u.setName(r.name());
        u.setEmail(r.email());
        u.setPassword(passwordEncoder.encode(r.password())); // plain password kabhi save nahi hota
        userRepository.save(u);
        return response(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = userRepository.findByEmail(r.email())
                .filter(x -> passwordEncoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!u.isActive()) throw new IllegalArgumentException("Account blocked");
        return response(u);
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwtService.generateToken(u.getEmail(), u.getRole().name()), u.getName(), u.getEmail(), u.getRole());
    }
}
