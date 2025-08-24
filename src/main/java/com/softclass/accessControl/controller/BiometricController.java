package com.softclass.accessControl.controller;

import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.dto.DailyAttendanceReport;
import com.softclass.accessControl.repo.AttendanceRepository;
import com.softclass.accessControl.service.VerificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BiometricController {

    private final VerificationService verificationService;
    private final AttendanceRepository attendanceRepository;

    public BiometricController(VerificationService verificationService,
                               AttendanceRepository attendanceRepository) {
        this.verificationService = verificationService;
        this.attendanceRepository = attendanceRepository;
    }

    @PostMapping("/enrolar/{id}")
    public String enrolar(@PathVariable Long id) {
        // solo mock
        return "Usuario " + id + " enrolado (mock).";
    }

    @PostMapping("/verificar/{id}")
    public Attendance verificar(@PathVariable Long id, @RequestParam(defaultValue = "IN") String type) {
        // type="IN" o "OUT"
        return verificationService.verifyAndSave(id, type.toUpperCase());
    }

    @GetMapping("/attendances")
    public List<Attendance> listAll() {
        return attendanceRepository.findAll();
    }

    @GetMapping("/reporte")
    public List<DailyAttendanceReport> reporte() {
        return verificationService.getDailyReports();
    }
}
