package ru.skillbox.socialnetwork.dialog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DialogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DialogServiceApplication.class, args);
	}
    //TODO:
    // mark read only those who belong to another user, by last message
    // first it shows wrong order, then correct one
    // weird order of dialogs fix
}
