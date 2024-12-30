package com.bartek.ecommerce.controller;

import com.bartek.ecommerce.dto.*;
import com.bartek.ecommerce.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid RegisterRequest registerRequest
    ) throws MessagingException {
        userService.registerUser(registerRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(
            @RequestParam("token") String token
    ) throws MessagingException {
        userService.activateAccount(token);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
