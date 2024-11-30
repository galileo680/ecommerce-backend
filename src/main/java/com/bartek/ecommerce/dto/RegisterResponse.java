package com.bartek.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    //private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}