package com.nftheater.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NfTheaterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NfTheaterApiApplication.class, args);
	}

}
