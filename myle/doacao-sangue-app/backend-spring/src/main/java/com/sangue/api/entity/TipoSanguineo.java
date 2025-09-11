package com.sangue.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TipoSanguineo {
    @JsonProperty("A+")
    A_POSITIVO,

    @JsonProperty("A-")
    A_NEGATIVO,

    @JsonProperty("B+")
    B_POSITIVO,

    @JsonProperty("B-")
    B_NEGATIVO,

    @JsonProperty("AB+")
    AB_POSITIVO,

    @JsonProperty("AB-")
    AB_NEGATIVO,

    @JsonProperty("O+")
    O_POSITIVO,

    @JsonProperty("O-")
    O_NEGATIVO
}
