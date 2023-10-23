package com.ssau.autotests_lab2.db.repository;

import com.ssau.autotests_lab2.db.model.CalculationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {
}
