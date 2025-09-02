package com.maxsoft.loginregister.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="usuarios")
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreusuario;
    private String contrasena;

    private String rol;

    @CreationTimestamp
    @Column(name="creado_en", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name="modificado_en")
    private LocalDateTime modificado;



}
