package com.sangue.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDate dataNascimento; // Armazena a data para validar a idade
}
