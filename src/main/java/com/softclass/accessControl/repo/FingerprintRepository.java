package com.softclass.accessControl.repo;

import com.softclass.accessControl.domain.Fingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {
    Optional<Fingerprint> findByPersonaId(Long personaId);
}

