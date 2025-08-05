package com.sangue.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO usado no login do usuário
 */
@Getter
@Setter
public class LoginDTO {

    private String email;
    private String senha;
}
