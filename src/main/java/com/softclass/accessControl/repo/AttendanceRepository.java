package com.softclass.accessControl.repo;

import com.softclass.accessControl.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);
}

