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

    @PostMapping("/enrolar/{id}")
    public String enrolar(@PathVariable Long id) {
        if (verificationService.isUseMock()) {
            return "Usuario " + id + " enrolado (mock).";
        } else {
            return "Usuario " + id + " enrolado en huellero real (pendiente SDK).";
        }
    }

    @PostMapping("/verificar/{id}")
    public Attendance verificar(@PathVariable Long id) {
        return verificationService.verifyAndSave(id);
    }

    @GetMapping("/attendances")
    public List<Attendance> listAll() {
        return verificationService.getAllAttendances();
    }

    @PostMapping("/attendances")
    public Attendance create(@RequestBody Attendance attendance) {
        return verificationService.createAttendance(attendance);
    }

    @GetMapping("/reporte")
    public List<VerificationService.DailyAttendanceReport> reporte() {
        return verificationService.getDailyReports();
    }
}
