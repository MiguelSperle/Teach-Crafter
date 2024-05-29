package com.miguelsperle.teach_crafter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeachCrafterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeachCrafterApplication.class, args);
	}

}
