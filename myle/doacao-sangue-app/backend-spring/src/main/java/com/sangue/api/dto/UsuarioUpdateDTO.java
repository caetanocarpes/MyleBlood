package com.sangue.api.dto;

import com.sangue.api.entity.TipoSanguineo;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para atualização parcial de usuário.
 * Todos os campos são opcionais; envie apenas o que mudou.
 */
@Getter
@Setter
public class UsuarioUpdateDTO {
    private String nome;

    @Email(message = "Formato de email inválido")
    private String email;

    private String cpf;   // sugiro bloquear no service (ou só admin pode alterar)
    private String senha; // para trocar senha direto (ou criar endpoint separado)

    private String dataNascimento; // yyyy-MM-dd

    private TipoSanguineo tipoSanguineo;

    @Digits(integer = 3, fraction = 2)
    @DecimalMin("30.00") @DecimalMax("300.00")
    private BigDecimal pesoKg;

    @Min(120) @Max(230)
    private Integer alturaCm;
}
