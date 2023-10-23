package com.ssau.autotests_lab2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssau.autotests_lab2.db.model.CalculationResult;
import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {IntegrationTests.Initializer.class})
@Testcontainers
public class IntegrationTests {
    @Autowired
    private CalculationResultRepository repo;
    @Autowired
    private ResourceLoader resourceLoader = null;
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
    public void animalsCountShouldBeCorrect() {
        long count = repo.count();
        assertEquals(3, count);
    }


}
