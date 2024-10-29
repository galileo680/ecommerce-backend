package com.bartek.ecommerce.controller;

import com.bartek.ecommerce.dto.LoginRequest;
import com.bartek.ecommerce.dto.LoginResponse;
import com.bartek.ecommerce.dto.UserDto;
import com.bartek.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid UserDto registrationRequest
    ) {
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }
}
