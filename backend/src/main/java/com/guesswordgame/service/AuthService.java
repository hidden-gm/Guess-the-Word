package com.guesswordgame.service;

import com.guesswordgame.dto.AuthDtos;
import com.guesswordgame.entity.Role;
import com.guesswordgame.entity.User;
import com.guesswordgame.repository.UserRepository;
import com.guesswordgame.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String USERNAME_REGEX = "^[A-Za-z]{5,40}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$%*]).{5,72}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String username = safe(request.username());
        String password = request.password();

        if (!username.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException("Username must contain only letters and be 5-40 characters long.");
        }
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Password must be at least 5 characters with letters, numbers, and one of $, %, *.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already registered.");
        }

        User user = userRepository.save(new User(username, passwordEncoder.encode(password), Role.PLAYER));
        UserDetails details = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthDtos.AuthResponse(jwtService.generateToken(details), user.getUsername(), user.getRole().name());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String username = safe(request.username());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.password()));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        UserDetails details = userDetailsService.loadUserByUsername(username);
        return new AuthDtos.AuthResponse(jwtService.generateToken(details), user.getUsername(), user.getRole().name());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
