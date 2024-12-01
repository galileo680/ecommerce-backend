package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.*;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.exception.InvalidCredentialsException;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.UserMapper;
import com.bartek.ecommerce.repository.RoleRepository;
import com.bartek.ecommerce.repository.UserRepository;
import com.bartek.ecommerce.security.JwtService;
import com.bartek.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public void registerUser(RegisterRequest registerRequest) {
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

}
