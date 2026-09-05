package com.pollingapp.controller;

import com.pollingapp.dto.AuthenticationResponse;
import com.pollingapp.dto.LoginRequest;
import com.pollingapp.dto.SignupRequest;
import com.pollingapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new account with email and password")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequest request) {
        String message = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive a JWT token")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
