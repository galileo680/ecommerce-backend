package com.bartek.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String token;
}
