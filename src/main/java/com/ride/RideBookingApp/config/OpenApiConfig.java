package com.ride.RideBookingApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("RideBookingApp REST APIs")
						.version("1.0.0")
						.description("Comprehensive REST APIs for RideBookingApp with Layered Architecture, DTOs, and Real-Time Dashboard."));
	}

}

