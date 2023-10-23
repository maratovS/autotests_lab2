package com.ssau.autotests_lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestAutotestsLab2Application {

    public static void main(String[] args) {
        SpringApplication.from(AutotestsLab2Application::main).with(TestAutotestsLab2Application.class).run(args);
    }

}
