package com.sangue.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade que representa um agendamento de doação
 */
@Entity
@Table(name = "agendamentos")
@Getter
@Setter
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuário que fez o agendamento
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Posto onde será feita a doação
    @ManyToOne
    @JoinColumn(name = "posto_id")
    private Posto posto;

    private LocalDate data;     // Data da doação
    private LocalTime horario;  // Horário da doação
}
