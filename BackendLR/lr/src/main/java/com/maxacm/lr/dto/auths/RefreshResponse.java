package com.maxacm.lr.dto.auths;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
    private String refreshToken;
}
