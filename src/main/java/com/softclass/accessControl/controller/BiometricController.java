package com.softclass.accessControl.controller;

import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.domain.Persona;
import com.softclass.accessControl.repo.PersonaRepository;
import com.softclass.accessControl.service.VerificationService;
import lombok.RequiredArgsConstructor;
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

    /**
     * Enrolar un usuario (mock/huellero) y crear Persona si no existe
     */
    @PostMapping("/enrolar/{id}")
    public ResponseEntity<String> enrolar(@PathVariable Long id) {
        Persona persona = personaRepository.findById(id).orElseGet(() -> {
            // Crear automáticamente persona desconocida si no existe
            Persona p = new Persona();
            p.setId(id);
            p.setName("Desconocido");
            p.setDocument("unknown-" + id);
            p.setRole("USER");
            return personaRepository.save(p);
        });

        return ResponseEntity.ok("Usuario " + persona.getName() + " enrolado correctamente (mock).");
    }

    /**
     * Crear persona manualmente
     */
    @PostMapping("/personas")
    public ResponseEntity<?> crearPersona(@RequestBody Persona persona) {
        // Validar duplicado por documento
        Optional<Persona> existing = personaRepository.findByDocument(persona.getDocument());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Persona con documento ya existe: " + persona.getDocument());
        }
        Persona saved = personaRepository.save(persona);
        return ResponseEntity.ok(saved);
    }

    /**
     * Editar persona existente
     */
    @PutMapping("/personas/{id}")
    public ResponseEntity<?> editarPersona(@PathVariable Long id, @RequestBody Persona persona) {
        Optional<Persona> existing = personaRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
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
    public List<Persona> findAll(){
     return personaRepository.findAll();
    }

    /**
     * Verificar huella → marcar entrada/salida automáticamente
     */
    @PostMapping("/verificar/{id}")
    public Attendance verificar(@PathVariable Long id) {
        return verificationService.verifyAndSave(id);
    }

    /**
     * Listar todas las asistencias
     */
    @GetMapping("/attendances")
    public List<Attendance> listAll() {
        return verificationService.getAllAttendances();
    }

    /**
     * Crear asistencia manualmente (opcional)
     */
    @PostMapping("/attendances")
    public Attendance create(@RequestBody Attendance attendance) {
        return verificationService.createAttendance(attendance);
    }

    /**
     * Generar reporte diario de asistencias
     */
    @GetMapping("/reporte")
    public List<VerificationService.DailyAttendanceReport> reporte() {
        return verificationService.getDailyReports();
    }

}
