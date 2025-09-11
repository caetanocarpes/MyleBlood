package com.sangue.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO usado no login do usuário
 * - Validações básicas (email e senha obrigatórios)
 */
@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
