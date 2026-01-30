package ru.skillbox.socialnetwork.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@ComponentScan(
        basePackages = {"ru.skillbox.socialnetwork", "com.team58"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }
}
