package com.maxacm.lr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;      // Usuario que hizo la acción
    private String action;        // LOGIN, REFRESH, LOGOUT
    private String ip;            // Dirección IP
    private LocalDateTime timestamp; // Fecha/hora
}
