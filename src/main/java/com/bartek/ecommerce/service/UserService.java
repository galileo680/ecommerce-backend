package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.*;
import com.bartek.ecommerce.entity.User;

import java.util.List;

public interface UserService {

    void registerUser(RegisterRequest registerRequest);
    LoginResponse loginUser(LoginRequest loginRequest);
    List<UserDto> getAllUsers();
    User getCurrentUser();
}
