package ru.fastdelivery.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "ru.fastdelivery" })
@EnableAutoConfiguration
public class Config {
}
