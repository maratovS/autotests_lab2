package com.ssau.autotests_lab2.service;

import com.ssau.autotests_lab2.db.model.CalculationResult;
import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import com.ssau.autotests_lab2.dto.CalculationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mariuszgromada.math.mxparser.Expression;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalculationService {

    @Autowired
    CalculationResultRepository repo;

    public CalculationDto calculate(CalculationDto calculationDto) {
        Expression e = new Expression(Integer.parseInt(calculationDto.getFirstNum(), calculationDto.getFirstNumSystem())
                + calculationDto.getOperation() + Integer.parseInt(calculationDto.getSecondNum(), calculationDto.getSecondNumSystem()));
        int result = (int) e.calculate();
        calculationDto.setResult(Integer.toString(result, calculationDto.getFirstNumSystem()));
        System.out.println(calculationDto.getResult());
        return calculationDto;
    }

    public CalculationDto getResult(String firstNum, String secondNum, String operation, int system) {
        CalculationDto dto = new CalculationDto();
        dto.setExecutionTime(LocalDateTime.now());
        dto.setFirstNum(firstNum);
        dto.setFirstNumSystem(system);
        dto.setSecondNum(secondNum);
        dto.setSecondNumSystem(system);
        dto.setOperation(operation);
        return this.calculate(dto);
    }

    public List<CalculationResult> getByTime(LocalDateTime start, LocalDateTime end){
        return repo.findAllByExecutionTimeBetween(start, end);
    }
}
