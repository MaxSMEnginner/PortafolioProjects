package com.maxacm.lr.dto.auths;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    // Constructor solo para error/mensaje (1 parámetro → no choca)
    public AuthResponse(String message) {
        this.accessToken = message;
        this.refreshToken = null;
    }
}
