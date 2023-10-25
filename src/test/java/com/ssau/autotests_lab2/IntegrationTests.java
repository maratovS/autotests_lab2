package com.ssau.autotests_lab2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssau.autotests_lab2.db.model.CalculationResult;
import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(initializers = {IntegrationTests.Initializer.class})
@Testcontainers
public class IntegrationTests {
    @Autowired
    private CalculationResultRepository repo;
    @Autowired
    private ResourceLoader resourceLoader = null;
    
    private final int RECORDS_COUNT = 28;
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.liquibase.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeEach
    public void addCalculationResults() throws IOException {
        File dataFile = resourceLoader.getResource("classpath:init.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        CalculationResult[] results = mapper.readValue(dataFile, CalculationResult[].class);
        repo.saveAll(Arrays.stream(results).toList());
    }

    @Test
    @Transactional
    public void checkCalculationResults(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }

    @Test
    @Transactional
    public void recordDeleteTest(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);
        repo.delete(repo.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException(String.format("Record not found on test \"%s\"", testInfo.getDisplayName()))));
        count = repo.count();
        assertEquals(27, count);
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }


    @Test
    @Transactional
    public void allRecordsDeleteTest(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);
        repo.deleteAll();
        count = repo.count();
        assertEquals(0, count);
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }

    @Test
    @Transactional
    public void addRecordDeleteTest(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);

        repo.save(new CalculationResult(
                null,
                "1",
                2,
                "1",
                2,
                "10",
                "+" ,
                LocalDateTime.now()

        ));

        count = repo.count();
        assertEquals(RECORDS_COUNT + 1, count);
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }

    @Test
    @Transactional
    public void addRecordsDeleteTest(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);

        for (int i = 0; i < 10; i++) {
            repo.save(new CalculationResult(
                    null,
                    String.valueOf(i),
                    10,
                    String.valueOf(i),
                    10,
                    String.valueOf(i + i),
                    "+" ,
                    LocalDateTime.now()

            ));
        }

        count = repo.count();
        assertEquals(28 +10, count);
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }

    @Test
    @Transactional
    public void updateRecordDeleteTest(TestInfo testInfo) {
        System.out.printf("Starting test -  \"%s\"%n", testInfo.getDisplayName());
        long count = repo.count();
        assertEquals(RECORDS_COUNT, count);

        Optional<CalculationResult> optFound = repo.findAll().stream().findFirst();
        assertTrue(optFound.isPresent());
        CalculationResult found = optFound.get();
        found.setExecutionTime(LocalDateTime.now());
        repo.save(found);

        Optional<CalculationResult> optUpdated = repo.findById(found.getId());
        assertTrue(optUpdated.isPresent());
        CalculationResult updated = optFound.get();
        assertEquals(found.getExecutionTime(), updated.getExecutionTime());
        System.out.printf("Finishing test -  \"%s\"", testInfo.getDisplayName());
    }
}
