package com.sangue.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de criação de agendamento.
 * data = yyyy-MM-dd
 * horario = HH:mm (24h)
 */
@Getter
@Setter
public class AgendamentoDTO {

    @NotNull(message = "Posto é obrigatório")
    private Long postoId;

    @NotNull(message = "Data é obrigatória (yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data deve estar no formato yyyy-MM-dd")
    private String data;

    @NotNull(message = "Horário é obrigatório (HH:mm)")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Horário deve estar no formato HH:mm")
    private String horario;
}
