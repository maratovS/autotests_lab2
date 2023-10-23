package com.ssau.autotests_lab2.service;

import com.ssau.autotests_lab2.dto.CalculationDto;
import org.springframework.stereotype.Service;
import org.mariuszgromada.math.mxparser.Expression;

@Service
public class CalculationService {
    
    public CalculationDto calculate(CalculationDto calculationDto) {
        Expression e = new Expression(Integer.parseInt(calculationDto.getFirstNum(), calculationDto.getFirstNumSystem())
                + calculationDto.getOperation() + Integer.parseInt(calculationDto.getSecondNum(), calculationDto.getSecondNumSystem()));
         calculationDto.setResult(String.valueOf((int)e.calculate()));
        System.out.println(calculationDto.getResult());
         return calculationDto;
    }
}
