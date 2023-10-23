package com.ssau.autotests_lab2.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CalculationDto {

    private String id;
    private String firstNum;
    private Integer firstNumSystem;
    private String secondNum;
    private Integer secondNumSystem;
    private String operation;
    private String result;
    private LocalDateTime executionTime;
}
