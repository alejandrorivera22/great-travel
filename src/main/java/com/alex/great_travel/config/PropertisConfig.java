package com.alex.great_travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = "classpath:reddis.properties"
)
public class PropertisConfig {
}
