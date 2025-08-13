package com.dmt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DmtAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmtAppApplication.class, args);
	}

}
