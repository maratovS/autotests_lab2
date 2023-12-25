package com.ssau.autotests_lab2.db.repository;

import com.ssau.autotests_lab2.db.model.CalculationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {
    List<CalculationResult> findAllByExecutionTimeBetween(LocalDateTime start, LocalDateTime end);
}
