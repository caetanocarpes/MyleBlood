package com.sangue.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum de tipos sanguíneos com mapeamento JSON amigável:
 * - Aceita "A+", "O-", etc. no payload JSON
 * - Armazena no banco como A_POS, O_NEG, etc.
 */
public enum TipoSanguineo {
    @JsonProperty("A+")
    A_POS,

    @JsonProperty("A-")
    A_NEG,

    @JsonProperty("B+")
    B_POS,

    @JsonProperty("B-")
    B_NEG,

    @JsonProperty("AB+")
    AB_POS,

    @JsonProperty("AB-")
    AB_NEG,

    @JsonProperty("O+")
    O_POS,

    @JsonProperty("O-")
    O_NEG
}
