package com.example.congestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class CongestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongestionApplication.class, args);
	}

}
