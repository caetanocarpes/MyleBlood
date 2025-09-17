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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;
    private String cidade;
    private String estado;
    private String horarioFuncionamento;
}
