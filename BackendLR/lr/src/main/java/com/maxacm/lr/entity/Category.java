package com.maxacm.lr.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.maxacm.lr.Enum.TypeTransaction;
import java.util.Set;



@Entity
@Data
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Define si la categoría es para incomes o outcomes
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransaction type;

    // Si decides que las categorías son globales, puedes omitir la relación con User.
    // Si cada usuario tiene sus propias categorías (más flexible), úsala:

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    // Relación One-to-Many con Transaction
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;
}