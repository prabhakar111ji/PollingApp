package com.pollingapp.service;

import com.pollingapp.dto.AuthenticationResponse;
import com.pollingapp.dto.LoginRequest;
import com.pollingapp.dto.SignupRequest;
import com.pollingapp.entity.User;
import com.pollingapp.exception.ConflictException;
import com.pollingapp.repository.UserRepository;
import com.pollingapp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService covering registration and login flows.
 * All dependencies (UserRepository, PasswordEncoder, JwtService, AuthenticationManager)
 * are mocked to isolate authentication business logic.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("John", "Doe", "john@test.com", "password123");
        loginRequest = new LoginRequest("john@test.com", "password123");

        savedUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("$2a$10$encodedPassword")
                .role("USER")
                .build();
    }

    // ─── Signup ──────────────────────────────────────────────────

    @Test
    @DisplayName("signup — should register new user successfully")
    void signup_newUser_returnsSuccessMessage() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        String result = authService.signup(signupRequest);

        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("john@test.com") &&
                user.getPassword().equals("$2a$10$encodedPassword") &&
                user.getRole().equals("USER")
        ));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("signup — should reject duplicate email")
    void signup_duplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("signup — should hash password with BCrypt encoder")
    void signup_hashesPassword_neverStoresPlaintext() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedValue");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        authService.signup(signupRequest);

        verify(userRepository).save(argThat(user ->
                !user.getPassword().equals("password123") &&
                user.getPassword().startsWith("$2a$10$")
        ));
    }

    // ─── Login ───────────────────────────────────────────────────

    @Test
    @DisplayName("login — should authenticate and return JWT token")
    void login_validCredentials_returnsTokenAndUserInfo() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-123");
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(savedUser));

        AuthenticationResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("login — should reject invalid credentials")
    void login_invalidCredentials_throwsBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
