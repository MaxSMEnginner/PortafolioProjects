package com.maxacm.lr.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;


@Entity
@Data
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Monto del movimiento


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate date;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)" )
    private TypeTransaction type; // INCOME O OUTCOME (del Enum)


    @PrePersist
    @PreUpdate
    public void SincronizeTypewithCategoryType(){
        if(category != null){
            this.type = category.getType();
        }
    }

    // --- Relaciones Many-to-One (Claves Foráneas) ---

    // FK a Usuario: Quién hizo la transacción (Seguridad/Filtro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // FK a Cuenta: Dónde se originó/terminó el dinero
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // FK a Categoría: Por qué se movió el dinero
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}