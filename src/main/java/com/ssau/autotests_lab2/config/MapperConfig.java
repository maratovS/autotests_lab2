package com.ssau.autotests_lab2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class MapperConfig {
    @SuppressWarnings("Convert2Lambda")
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        Converter<Date, LocalDateTime> dateLocalDateTimeConverter = new Converter<>() {

            @Override
            public LocalDateTime convert(MappingContext<Date, LocalDateTime> context) {

                return context.getSource().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        };
        modelMapper.addConverter(dateLocalDateTimeConverter);
        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
