package com.sangue.api.dto;

import com.sangue.api.entity.TipoSanguineo;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para criação de usuário.
 * Inclui validações com Bean Validation para garantir consistência
 * antes de chegar no service.
 */
@Getter
@Setter
public class UsuarioDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @NotBlank(message = "Data de nascimento é obrigatória (formato yyyy-MM-dd)")
    private String dataNascimento; // validado no service (idade 16–69)

    // ===== Novos campos =====
    @NotNull(message = "Tipo sanguíneo é obrigatório")
    private TipoSanguineo tipoSanguineo;

    @NotNull(message = "Peso é obrigatório")
    @Digits(integer = 3, fraction = 2)
    @DecimalMin(value = "30.00", message = "Peso mínimo: 30 kg")
    @DecimalMax(value = "300.00", message = "Peso máximo: 300 kg")
    private BigDecimal pesoKg;

    @NotNull(message = "Altura é obrigatória")
    @Min(value = 120, message = "Altura mínima: 120 cm")
    @Max(value = 230, message = "Altura máxima: 230 cm")
    private Integer alturaCm;
}
