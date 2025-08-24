package com.softclass.accessControl.service;

import com.softclass.accessControl.domain.Fingerprint;
import com.softclass.accessControl.domain.Persona;
import com.softclass.accessControl.repo.FingerprintRepository;
import com.softclass.accessControl.repo.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private final BiometricService biometricService;
    private final PersonaRepository personaRepo;
    private final FingerprintRepository fingerprintRepo;

    public EnrollmentService(BiometricService biometricService,
                             PersonaRepository personaRepo,
                             FingerprintRepository fingerprintRepo) {
        this.biometricService = biometricService;
        this.personaRepo = personaRepo;
        this.fingerprintRepo = fingerprintRepo;
    }

    @Transactional
    public void enroll(Long personaId) {
        Persona p = personaRepo.findById(personaId)
                .orElseThrow(() -> new IllegalArgumentException("Persona no existe: " + personaId));

        byte[] tpl = biometricService.captureTemplate(String.valueOf(personaId));

        Fingerprint fp = fingerprintRepo.findByPersonaId(personaId)
                .orElseGet(Fingerprint::new);

        fp.setPersona(p);
        fp.setFormat(biometricService.format());
        fp.setTemplate(tpl);
        fingerprintRepo.save(fp);
    }
}
