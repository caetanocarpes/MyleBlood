package com.sangue.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO usado para transferência de dados de agendamento
 */
@Getter
@Setter
public class AgendamentoDTO {

    private Long postoId;     // ID do posto de doação
    private String data;      // Data do agendamento no formato "yyyy-MM-dd"
    private String horario;   // Horário no formato "HH:mm"
}
