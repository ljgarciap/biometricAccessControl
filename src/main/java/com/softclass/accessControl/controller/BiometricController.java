package com.softclass.accessControl.controller;

import com.softclass.accessControl.biometric.BiometricDevice;
import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.domain.Persona;
import com.softclass.accessControl.repo.PersonaRepository;
import com.softclass.accessControl.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BiometricController {

    private final VerificationService verificationService;
    private final PersonaRepository personaRepository;
    private final BiometricDevice biometricDevice;

    /**
     * Enrolar huella y crear Persona si no existe
     */
    @PostMapping("/enrolar/{document}")
    public ResponseEntity<String> enrolar(@PathVariable String document) {
        Persona persona = personaRepository.findByDocument(document)
                .orElseGet(() -> {
                    Persona p = new Persona();
                    p.setDocument(document);
                    p.setName("Desconocido");
                    p.setRole("USER");
                    return personaRepository.save(p);
                });

        boolean success = biometricDevice.enroll(document);
        if (!success) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enrolar huella para " + document);
        }

        return ResponseEntity.ok("Huella enrolada para " + persona.getName());
    }

    /**
     * Verificar huella y registrar asistencia
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificar() {
        String document = biometricDevice.verify();
        if (document == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Huella no reconocida");
        }

        Optional<Persona> persona = personaRepository.findByDocument(document);
        if (persona.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Persona con documento " + document + " no existe");
        }

        Attendance attendance = verificationService.verifyAndSave(persona.get().getId());
        return ResponseEntity.ok(attendance);
    }

    /**
     * Crear persona manual
     */
    @PostMapping("/personas")
    public ResponseEntity<?> crearPersona(@RequestBody Persona persona) {
        Optional<Persona> existing = personaRepository.findByDocument(persona.getDocument());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Persona con documento ya existe: " + persona.getDocument());
        }
        Persona saved = personaRepository.save(persona);
        return ResponseEntity.ok(saved);
    }

    /**
     * Editar persona
     */
    @PutMapping("/personas/{id}")
    public ResponseEntity<?> editarPersona(@PathVariable Long id, @RequestBody Persona persona) {
        Optional<Persona> existing = personaRepository.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Persona p = existing.get();
        p.setName(persona.getName());
        p.setDocument(persona.getDocument());
        p.setRole(persona.getRole());
        Persona saved = personaRepository.save(p);
        return ResponseEntity.ok(saved);
    }

    /**
     * Listar personas
     */
    @GetMapping("/personas")
    public List<Persona> findAll() {
        return personaRepository.findAll();
    }

    /**
     * Listar todas las asistencias
     */
    @GetMapping("/attendances")
    public List<Attendance> listAll() {
        return verificationService.getAllAttendances();
    }

    /**
     * Crear asistencia manual
     */
    @PostMapping("/attendances")
    public Attendance create(@RequestBody Attendance attendance) {
        return verificationService.createAttendance(attendance);
    }

    /**
     * Reporte diario
     */
    @GetMapping("/reporte")
    public List<VerificationService.DailyAttendanceReport> reporte() {
        return verificationService.getDailyReports();
    }
}
