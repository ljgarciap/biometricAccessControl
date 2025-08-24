package com.softclass.accessControl.service;

import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.domain.Persona;
import com.softclass.accessControl.dto.DailyAttendanceReport;
import com.softclass.accessControl.repo.AttendanceRepository;
import com.softclass.accessControl.repo.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VerificationService {

    private final AttendanceRepository attendanceRepository;
    private final PersonaRepository personaRepository;

    public VerificationService(AttendanceRepository attendanceRepository,
                               PersonaRepository personaRepository) {
        this.attendanceRepository = attendanceRepository;
        this.personaRepository = personaRepository;
    }

    @Transactional
    public Attendance verifyAndSave(Long userId, String type) {
        // Guarda la asistencia
        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setType(type);
        attendance.setTimestamp(LocalDateTime.now());
        attendance.setIncomplete(true); // asumimos incompleto hasta procesar
        attendanceRepository.save(attendance);

        // Revisa todas las marcas del día
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<Attendance> todays = attendanceRepository.findByUserIdAndTimestampBetween(userId, start, end);

        long ins = todays.stream().filter(a -> "IN".equalsIgnoreCase(a.getType())).count();
        long outs = todays.stream().filter(a -> "OUT".equalsIgnoreCase(a.getType())).count();

        // Si hay al menos una entrada y una salida, marca todas como complete
        if (ins > 0 && outs > 0) {
            todays.forEach(a -> a.setIncomplete(false));
            attendanceRepository.saveAll(todays);
            attendance.setIncomplete(false);
        }

        return attendance;
    }

    public List<DailyAttendanceReport> getDailyReports() {
        // obtenemos todas las asistencias agrupadas por usuario y día
        List<Attendance> all = attendanceRepository.findAll();

        Map<Long, Map<LocalDate, List<Attendance>>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        Attendance::getUserId,
                        Collectors.groupingBy(a -> a.getTimestamp().toLocalDate())
                ));

        List<DailyAttendanceReport> reports = new ArrayList<>();
        for (Map.Entry<Long, Map<LocalDate, List<Attendance>>> userEntry : grouped.entrySet()) {
            Long userId = userEntry.getKey();
            Persona p = personaRepository.findById(userId).orElse(new Persona(userId, "Desconocido", "", ""));
            for (Map.Entry<LocalDate, List<Attendance>> dayEntry : userEntry.getValue().entrySet()) {
                List<Attendance> list = dayEntry.getValue();
                int ins = (int) list.stream().filter(a -> "IN".equalsIgnoreCase(a.getType())).count();
                int outs = (int) list.stream().filter(a -> "OUT".equalsIgnoreCase(a.getType())).count();
                List<LocalDateTime> timestamps = list.stream()
                        .map(Attendance::getTimestamp)
                        .sorted()
                        .toList();
                boolean incomplete = list.stream().anyMatch(Attendance::getIncomplete);
                reports.add(new DailyAttendanceReport(
                        userId,
                        p.getName(),
                        dayEntry.getKey(),
                        ins,
                        outs,
                        timestamps,
                        incomplete
                ));
            }
        }

        return reports;
    }
}
