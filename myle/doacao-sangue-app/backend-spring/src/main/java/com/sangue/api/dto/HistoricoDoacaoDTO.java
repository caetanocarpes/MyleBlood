package com.sangue.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para exibir o histórico de doações de um usuário
 */
@Getter
@Setter
@AllArgsConstructor
public class HistoricoDoacaoDTO {

    // Nome do posto onde a doação foi feita
    private String nomePosto;

    // Cidade do posto
    private String cidade;

    // Estado do posto
    private String estado;

    // Data da doação (formato: yyyy-MM-dd)
    private String data;

    // Horário da doação (formato: HH:mm)
    private String horario;
}
