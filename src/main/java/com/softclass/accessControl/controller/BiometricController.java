package com.softclass.accessControl.controller;

import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BiometricController {

    private final VerificationService verificationService;

    /**
     * Enrolar un usuario (simulado)
     */
    @PostMapping("/enrolar/{id}")
    public String enrolar(@PathVariable Long id) {
        // Simulación: no persiste nada real
        return "Usuario " + id + " enrolado (mock).";
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
     * Crear una asistencia manualmente (opcional)
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
