package com.pisethjavaschool.userservice.config;

import java.time.ZoneId;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import com.pisethjavaschool.userservice.config.converter.InstantQueryParamConverter;

@Configuration
public class WebFluxConversionConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new InstantQueryParamConverter(ZoneId.of("Asia/Phnom_Penh")));
    }
}
