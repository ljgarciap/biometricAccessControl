package com.softclass.accessControl.repo;

import com.softclass.accessControl.domain.Fingerprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {
        List<Fingerprint> findByPersonaId(Long personaId);
    }
