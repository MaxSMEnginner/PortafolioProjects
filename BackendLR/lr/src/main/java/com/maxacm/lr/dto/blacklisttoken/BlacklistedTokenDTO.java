package com.maxacm.lr.dto.blacklisttoken;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class BlacklistedTokenDTO {
    private Long id;
    private String token;
    private LocalDateTime createdAt;
}
