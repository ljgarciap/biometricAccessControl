package com.softclass.accessControl.service;

import com.softclass.accessControl.biometric.BiometricDevice;
import com.softclass.accessControl.domain.Attendance;
import com.softclass.accessControl.domain.Persona;
import com.softclass.accessControl.repo.AttendanceRepository;
import com.softclass.accessControl.repo.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final AttendanceRepository attendanceRepository;
    private final PersonaRepository personaRepository;
    private final BiometricDevice biometricDevice;

    @Value("${biometric.provider:mock}")
    private String provider;

    @Transactional
    public Attendance verifyAndSave(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Attendance> todays = attendanceRepository.findByUserIdAndTimestampBetween(userId, startOfDay, endOfDay);
        todays.forEach(a -> { if (a.getIncomplete() == null) a.setIncomplete(false); });

        String type = "IN";
        if (!todays.isEmpty()) {
            Attendance last = todays.get(todays.size() - 1);
            type = "IN".equalsIgnoreCase(last.getType()) ? "OUT" : "IN";
        }

        // Registrar huella en sistema si no es mock
        if (!"mock".equalsIgnoreCase(provider)) {
            System.out.println("Verificación con SDK: " + provider);
        }

        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setType(type);
        attendance.setTimestamp(now);
        attendance.setIncomplete(true);
        attendanceRepository.save(attendance);

        // Re-evaluar incomplete
        List<Attendance> updatedList = attendanceRepository.findByUserIdAndTimestampBetween(userId, startOfDay, endOfDay);
        boolean hasIn = updatedList.stream().anyMatch(a -> "IN".equalsIgnoreCase(a.getType()));
        boolean hasOut = updatedList.stream().anyMatch(a -> "OUT".equalsIgnoreCase(a.getType()));
        if (hasIn && hasOut) {
            updatedList.forEach(a -> a.setIncomplete(false));
            attendanceRepository.saveAll(updatedList);
        }

        return attendance;
    }

    @Transactional(readOnly = true)
    public List<DailyAttendanceReport> getDailyReports() {
        List<Attendance> all = attendanceRepository.findAll();
        all.forEach(a -> { if (a.getIncomplete() == null) a.setIncomplete(false); });

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
                list.forEach(a -> { if (a.getIncomplete() == null) a.setIncomplete(false); });

                int ins = (int) list.stream().filter(a -> "IN".equalsIgnoreCase(a.getType())).count();
                int outs = (int) list.stream().filter(a -> "OUT".equalsIgnoreCase(a.getType())).count();
                List<LocalDateTime> timestamps = list.stream().map(Attendance::getTimestamp).sorted().toList();
                boolean incomplete = list.stream().anyMatch(a -> Boolean.TRUE.equals(a.getIncomplete()));

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

    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendances() {
        List<Attendance> all = attendanceRepository.findAll();
        all.forEach(a -> { if (a.getIncomplete() == null) a.setIncomplete(false); });
        return all;
    }

    @Transactional
    public Attendance createAttendance(Attendance attendance) {
        if (attendance.getIncomplete() == null) attendance.setIncomplete(false);
        return attendanceRepository.save(attendance);
    }

    public record DailyAttendanceReport(
            Long userId,
            String name,
            LocalDate date,
            int inCount,
            int outCount,
            List<LocalDateTime> timestamps,
            boolean incomplete
    ) {}
}
