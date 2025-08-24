package com.softclass.accessControl.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DailyAttendanceReport(
        Long userId,
        String name,
        LocalDate date,
        int totalIns,
        int totalOuts,
        List<LocalDateTime> timestamps,
        boolean incomplete
) {}
