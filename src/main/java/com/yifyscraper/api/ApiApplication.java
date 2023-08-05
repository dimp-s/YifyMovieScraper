package com.yifyscraper.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		//enable desktop support
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(ApiApplication.class, args);
	}

}
