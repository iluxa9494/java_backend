package ru.skillbox.socialnetwork.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@EnableAsync
public class McAccountApplication {

	public static void main(String[] args) {
		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
		SpringApplication.run(McAccountApplication.class, args);
	}

}
