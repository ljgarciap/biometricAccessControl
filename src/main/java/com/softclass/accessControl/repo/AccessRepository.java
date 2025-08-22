package com.softclass.accessControl.repo;

import com.softclass.accessControl.domain.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessRepository extends JpaRepository<Access, Long> {
        List<Access> findByDateBetween(LocalDateTime from, LocalDateTime to);

        @Query("select i.result, count(i) from Access i where i.date between :from and :to group by i.result")
        List<Object[]> countByResult(LocalDateTime from, LocalDateTime to);
    }
