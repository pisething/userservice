package com.pisethjavaschool.userservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ResetPinSessionProperties.class)
public class ResetPinSessionConfig {
}