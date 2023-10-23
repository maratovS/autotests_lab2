package com.ssau.autotests_lab2.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@SuppressWarnings("Convert2Diamond")
@Configuration
public class MapperConfig {
    @SuppressWarnings("Convert2Lambda")
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        Converter<Date, LocalDate> dateLocalDateTimeConverter = new Converter<>() {

            @Override
            public LocalDate convert(MappingContext<Date, LocalDate> context) {

                return context.getSource().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        };
        modelMapper.addConverter(dateLocalDateTimeConverter);
        return modelMapper;
    }
}
