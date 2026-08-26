package com.galileo.ecommerce.user.api;

import com.galileo.ecommerce.user.api.dto.IdResponse;
import com.galileo.ecommerce.user.api.dto.LoginRequest;
import com.galileo.ecommerce.user.api.dto.RefreshRequest;
import com.galileo.ecommerce.user.api.dto.RegisterRequest;
import com.galileo.ecommerce.user.api.dto.TokenResponse;
import com.galileo.ecommerce.user.application.AuthService;
import com.galileo.ecommerce.user.application.TokenPair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Registration and token management")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account")
    ResponseEntity<IdResponse> register(@Valid @RequestBody RegisterRequest request) {
        UUID id = authService.register(request.email(), request.password(), request.firstName(), request.lastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @PostMapping("/login")
    @Operation(summary = "Exchange email and password for an access and a refresh token")
    TokenResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        TokenPair tokens = authService.login(request.email(), request.password(), http.getRemoteAddr());
        return new TokenResponse(tokens.accessToken(), tokens.refreshToken());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate a refresh token and get a fresh token pair")
    TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair tokens = authService.refresh(request.refreshToken());
        return new TokenResponse(tokens.accessToken(), tokens.refreshToken());
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate a refresh token")
    ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
