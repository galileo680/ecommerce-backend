package com.bartek.ecommerce.mapper;


import com.bartek.ecommerce.dto.UserDto;
import com.bartek.ecommerce.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .build();

        return userDto;
    }
}
