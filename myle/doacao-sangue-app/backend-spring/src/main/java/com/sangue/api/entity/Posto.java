package com.sangue.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um posto de doação
 */
@Entity
@Table(name = "postos")
@Getter
@Setter
public class Posto {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;                // Nome do posto
    private String endereco;            // Endereço completo
    private String cidade;              // Cidade onde está o posto
    private String estado;              // Estado (UF)
    private String horarioFuncionamento; // Texto descritivo do horário
}
