package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserResponse {

	private String message;
	private String email;
	private boolean isEmailVerified;

	public RegisterUserResponse() {
	}
}
