package com.maxsoft.loginregister.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="listanegra_tokens")
public class ListanegraToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name="fecha_expiracion", updatable = false)
    private LocalDateTime fechaexpiracion;

    @Column(nullable = false, unique = true, length = 500)
    private String token;


}
