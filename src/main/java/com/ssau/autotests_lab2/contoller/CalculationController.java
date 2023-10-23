package com.ssau.autotests_lab2.contoller;

import com.ssau.autotests_lab2.db.model.CalculationResult;
import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import com.ssau.autotests_lab2.dto.CalculationDto;
import com.ssau.autotests_lab2.service.CalculationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CalculationController {
    @Autowired
    private CalculationService service;

    @Autowired
    CalculationResultRepository repo;

    @Autowired
    private ModelMapper mapper;

    @GetMapping(value = "/{id}")
    public ResponseEntity<CalculationDto> getCalculationById(@PathVariable Long id) {

        CalculationResult found = repo.findById(id).orElseThrow(() -> new RuntimeException(String.format("%s by id %d is not found", CalculationResult.class.getSimpleName(), id)));
        return ResponseEntity.ok().body( mapper.map(found, CalculationDto.class));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<CalculationDto>> getCalculationList() {

        List<CalculationDto> results = repo.findAll().stream().map(result -> mapper.map(result, CalculationDto.class)).toList();

        return ResponseEntity.ok().body(results);
    }

    @GetMapping(value = "/calculate")
    public ResponseEntity<CalculationDto> getCalculation(@RequestBody CalculationDto calculationDto) {
        calculationDto = service.calculate(calculationDto);
        CalculationResult toEntity = mapper.map(calculationDto, CalculationResult.class);
        toEntity.setExecutionTime(LocalDateTime.now());
        repo.save(toEntity);
        return ResponseEntity.ok().body( mapper.map(toEntity, CalculationDto.class));
    }
}
