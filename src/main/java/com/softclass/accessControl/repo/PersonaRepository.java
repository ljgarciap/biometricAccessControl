package com.softclass.accessControl.repo;

import com.softclass.accessControl.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByDocument(String document);
    List<Persona> findAll();
}
