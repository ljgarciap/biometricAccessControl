package com.softclass.accessControl.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Access {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        private Persona persona;

        private LocalDateTime date = LocalDateTime.now();
        private String device; // estación/lector
        private String result; // OK/FAIL
        private Integer score; // si aplica
    }
