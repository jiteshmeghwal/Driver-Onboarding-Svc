package com.example.driveronboardingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DriverOnboardingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverOnboardingServiceApplication.class, args);
	}

}
