package com.ssau.autotests_lab2.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer mapper() {
        return builder -> {
            DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
        };
    }
}
