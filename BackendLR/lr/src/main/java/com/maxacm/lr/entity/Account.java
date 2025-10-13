package com.maxacm.lr.entity;
import jakarta.persistence.*;
import lombok.*;
import com.maxacm.lr.Enum.TypeAccount;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@Table(name="accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAccount type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;




}
