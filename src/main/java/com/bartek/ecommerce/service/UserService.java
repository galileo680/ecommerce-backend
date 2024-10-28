package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.LoginRequest;
import com.bartek.ecommerce.dto.LoginResponse;
import com.bartek.ecommerce.dto.UserDto;
import com.bartek.ecommerce.entity.User;

import java.util.List;

public interface UserService {

    UserDto registerUser(UserDto registrationRequest);
    LoginResponse loginUser(LoginRequest loginRequest);
    List<UserDto> getAllUsers();
    User getCurrentUser();
}
