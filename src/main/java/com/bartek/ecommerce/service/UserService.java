package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.*;
import com.bartek.ecommerce.entity.User;
import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {

    void registerUser(RegisterRequest registerRequest) throws MessagingException;
    LoginResponse loginUser(LoginRequest loginRequest);
    List<UserDto> getAllUsers();
    User getCurrentUser();
    void activateAccount(String activationToken) throws MessagingException;
}
