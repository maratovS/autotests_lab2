package com.ssau.autotests_lab2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.dockerjava.api.exception.NotFoundException;
import com.ssau.autotests_lab2.db.model.CalculationResult;
import com.ssau.autotests_lab2.db.repository.CalculationResultRepository;
import com.ssau.autotests_lab2.dto.CalculationDto;
import io.cucumber.java.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = AutotestsLab2Application.class)
@CucumberContextConfiguration
public class CucumberTestsDefinition {

    @Autowired
    private CalculationResultRepository calculationRepository;

    @Getter
    private String status;

    @Getter
    private String getAllResult;

    @Getter
    private Response response;

    String calculationResult;

    @Getter
    private Response responseDate;

    @Autowired
    private ModelMapper modelMapper;

    public ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object defaultTransformer(Object fromValue, Type toValueType) {
        JavaType javaType = mapper.constructType(toValueType);
        return mapper.convertValue(fromValue, javaType);
    }

    @Given("^calculations table$")
    public void calculations_table(List<CalculationResult> dataTable) throws IOException {
        CalculationResult[] calculations = mapper.readValue(mapper.writeValueAsString(dataTable), CalculationResult[].class);
        calculationRepository.saveAll(Arrays.stream(calculations).toList());
    }


    @When("^I perform get request \"([^\"]*)\"$")
    public void getAllCalculations(String url) throws Exception {
        status = String.valueOf(RestAssured.given().get(url).getStatusCode());
        getAllResult = RestAssured.given().get(url).getBody().print();
    }

    @Then("I should receive response status code 200")
    public void responseAllStatusCode() {
        Assertions.assertEquals("200", getStatus());
    }

    @And("Body should contain calculations that are given into the table")
    public void responseAllBody() throws JsonProcessingException {
        Assertions.assertEquals(
                mapper.writeValueAsString(modelMapper.map(calculationRepository.findAll(), CalculationDto[].class)),
                getGetAllResult().replaceAll(" ", "T")
        );
    }

    @When("^I perform get request \"([^\"]*)\" with parameters (.+) and (.+) and (.+) and (.+)$")
    public void getResult(String url, String firstNum, String secondNum, String operation, String system) {
        calculationResult = String.valueOf(RestAssured.given().get(url + firstNum + "&" + secondNum + "&" + operation + "&" + system).getBody().prettyPrint());
    }

    @Then("^I see the result as (.+)$")
    public void checkResult(String result) {
        System.out.println("Result from get request: " + calculationResult);
        System.out.println("Result from example table: " + result);
        Assertions.assertEquals(result, calculationResult);
    }



    @When("^I perform get request \"([^\"]*)\" and I pass below body$")
    public void addNewCalculation(String url, CalculationDto createCalculationDto) throws JsonProcessingException {
        response = RestAssured.given()
                .header("Content-type", "application/json")
                .and().body(mapper.writeValueAsString(createCalculationDto))
                .when().get(url).then().extract().response();
    }

    @Then("I should receive status code 200")
    public void receiveStatusAfterAdding() {
        Assertions.assertEquals(200, getResponse().getStatusCode());
    }


    @And("I should receive body that given into the table")
    public void checkBodyAfterAdding() throws JsonProcessingException {
        CalculationResult calculationEntity = calculationRepository.findById(Long.valueOf(getResponse().jsonPath().getString("id"))).orElseThrow(() -> new NotFoundException("Calculation not found"));
        System.out.println(calculationEntity.getExecutionTime().getNano());
        Assertions.assertEquals(
                mapper.writeValueAsString(modelMapper.map(calculationEntity, CalculationDto.class)),
                getResponse().getBody().print()
        );
    }

    @When("^I perform get request \"([^\"]*)\" and I pass this date (.+) and below body$")
    public void postWithDate(String url, LocalDateTime date, CalculationDto createCalculationDto) throws ParseException, JsonProcessingException {
        createCalculationDto.setExecutionTime(date);
        responseDate = RestAssured.given()
                .header("Content-type", "application/json")
                .and().body(mapper.writeValueAsString(createCalculationDto))
                .when().get(url).then().extract().response();
    }

    @Then("I receive status code 200")
    public void receiveStatusAfterAddingWithDate() {
        Assertions.assertEquals(200, getResponseDate().getStatusCode());
    }

    @And("^I receive body that given into the table and with given date$")
    public void receiveBodyAndDate() throws JsonProcessingException {
        CalculationResult calculationEntity = calculationRepository.findById(Long.valueOf(getResponseDate().jsonPath().getString("id"))).orElseThrow(() -> new NotFoundException("Calculation not found"));
        CalculationDto dto = modelMapper.map(calculationEntity, CalculationDto.class);
        Assertions.assertEquals(
                mapper.writeValueAsString(dto),
                getResponseDate().getBody().print()
        );

        Assertions.assertEquals("2023-12-11T13:14:17", getResponseDate().jsonPath().getString("executionTime"));
    }

    @Before
    public void cleanRepo() {
        calculationRepository.deleteAll();
        System.out.println("database cleared");
    }
}
