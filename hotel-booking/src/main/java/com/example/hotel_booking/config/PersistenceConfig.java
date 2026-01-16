package com.example.hotel_booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.hotel_booking.repository.jpa")
@EnableMongoRepositories(basePackages = "com.example.hotel_booking.repository.mongo")
public class PersistenceConfig {
}
