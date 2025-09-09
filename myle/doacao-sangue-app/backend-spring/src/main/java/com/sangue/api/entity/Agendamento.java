package com.sangue.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade de agendamento de doação.
 * - Unique (posto, data, horario) para evitar conflito de horário no posto.
 * - Relaciona com usuário e posto.
 */
@Entity
@Table(name = "agendamentos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_agendamento_posto_data_horario",
                columnNames = {"posto_id", "data", "horario"}
        ))
@Getter
@Setter
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "posto_id", nullable = false)
    private Posto posto;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;
}
