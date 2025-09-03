package com.maxsoft.loginregister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="nuevas_tokens")
public class TokenNueva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500)
    private String token;
    private String nombreusuario;
    @CreationTimestamp
    @Column(name="fecha_expiracion", updatable = false)
    private LocalDateTime fechaexpiracion;
}
