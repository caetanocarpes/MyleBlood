package com.sangue.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade JPA do usuário.
 * - Email e CPF únicos
 * - Senha criptografada (BCrypt)
 * - Campos extras para critérios de doação de sangue
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, length = 11, nullable = false) // somente números
    private String cpf;

    @Column(nullable = false)
    private String senha;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    // ===== Novos campos =====
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo", nullable = false, length = 10)
    private TipoSanguineo tipoSanguineo;

    @Digits(integer = 3, fraction = 2) // até 999.99
    @DecimalMin("30.00")
    @DecimalMax("300.00")
    @Column(name = "peso_kg", precision = 6, scale = 2, nullable = false)
    private BigDecimal pesoKg;

    @Min(120)
    @Max(230)
    @Column(name = "altura_cm", nullable = false)
    private Integer alturaCm;

    // Setter para normalizar email (sempre lowercase)
    public void setEmail(String email) {
        this.email = (email != null) ? email.trim().toLowerCase() : null;
    }
}
