package com.softclass.accessControl.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fingerprint {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String finger; // p.ej. INDICE_DERECHO
        private String format; // ANSI_378, ISO_19794_2, DP_PROP

        @Lob
        @Column(columnDefinition = "VARBINARY")
        private byte[] template; // template biométrico

        @ManyToOne(optional = false)
        private Persona persona;
    }
