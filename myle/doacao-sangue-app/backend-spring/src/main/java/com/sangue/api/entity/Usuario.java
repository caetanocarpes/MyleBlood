package com.sangue.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true, length = 14)
    private String cpf;

    private String senha;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    // ===== Novos campos =====
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo", nullable = false, length = 10)
    private TipoSanguineo tipoSanguineo;

    @Digits(integer = 3, fraction = 2) // 0–999.99
    @DecimalMin("30.00")
    @DecimalMax("300.00")
    @Column(name = "peso_kg", precision = 6, scale = 2, nullable = false)
    private BigDecimal pesoKg;

    @Min(120)
    @Max(230)
    @Column(name = "altura_cm", nullable = false)
    private Integer alturaCm;
}
