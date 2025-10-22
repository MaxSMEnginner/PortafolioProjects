package com.maxacm.lr.dto.auditlog;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDTO {
    private Long id;
    private String username;      // Usuario que hizo la acción
    private String action;        // LOGIN, REFRESH, LOGOUT
    private String ip;            // Dirección IP
    private LocalDateTime timestamp; // Fecha/hora


}
