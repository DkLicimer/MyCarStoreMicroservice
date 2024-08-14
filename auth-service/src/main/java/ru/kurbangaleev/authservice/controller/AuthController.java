package ru.kurbangaleev.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kurbangaleev.authservice.dto.JwtResponse;
import ru.kurbangaleev.authservice.dto.LoginRequest;
import ru.kurbangaleev.authservice.dto.RegisterRequest;
import ru.kurbangaleev.authservice.dto.UserResponse;
import ru.kurbangaleev.authservice.model.User;
import ru.kurbangaleev.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getRoles());
        return ResponseEntity.ok(new UserResponse(user));
    }
}
