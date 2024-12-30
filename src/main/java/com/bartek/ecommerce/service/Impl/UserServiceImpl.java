package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.*;
import com.bartek.ecommerce.entity.ActivationToken;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.exception.InvalidCredentialsException;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.UserMapper;
import com.bartek.ecommerce.repository.RoleRepository;
import com.bartek.ecommerce.repository.ActivationTokenRepository;
import com.bartek.ecommerce.repository.UserRepository;
import com.bartek.ecommerce.security.JwtService;
import com.bartek.ecommerce.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivationTokenRepository activationTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    public void registerUser(RegisterRequest registerRequest) throws MessagingException {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email is already in use");
        }

        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));

        User user = User.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .roles(List.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        sendValidationEmail(user);

        log.info(savedUser.toString());

        //UserDto userDto = userMapper.toUserDto(savedUser);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("Email not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Password does not match");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toUserDto)
                .toList();

        return userDtos;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String  email = authentication.getName();

        log.info("User Email is: " + email);

        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public void activateAccount(String token) throws MessagingException {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(activationToken.getExpiresAt())) {
            sendValidationEmail(activationToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(activationToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        activationToken.setValidatedAt(LocalDateTime.now());
        activationTokenRepository.save(activationToken);
    }

    //Helper methods
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendAccountActivationEmail(
                user.getEmail(),
                user.getFirstname(),
                activationUrl + "?token=" + newToken
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // generate a token
        //String generatedToken = generateActivationCode(6);
        String generatedToken = UUID.randomUUID().toString();

        var token = ActivationToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        activationTokenRepository.save(token);
        return generatedToken;
    }

}








































