package com.softclass.accessControl.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class VerifyRequest {
   @NotNull
        private String documentId;    }
