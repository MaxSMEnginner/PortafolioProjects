package com.maxacm.lr.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.maxacm.lr.Enum.TypeTransactions.TypeTransaction;
import java.util.Set;



@Entity
@Data
@Table(name = "categorys")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Define si la categoría es para incomes o outcomes
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)" )
    private TypeTransaction type;

    // Si decides que las categorías son globales, puedes omitir la relación con User.
    // Si cada usuario tiene sus propias categorías (más flexible), úsala:

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;


    // Relación One-to-Many con Transaction
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Transaction> transactions;
}