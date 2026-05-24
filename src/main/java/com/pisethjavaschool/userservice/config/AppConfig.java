package com.pisethjavaschool.userservice.config;

import java.time.Clock;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.chaywang.userservice.config")
public class AppConfig {
	@Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
