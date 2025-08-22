package com.softclass.accessControl.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class EnrollRequest {
        @NotNull
        private Long personaId;
        @NotNull
        private String finger; // INDICE_DERECHO, etc.
    }
