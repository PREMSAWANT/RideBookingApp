package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserRequest {

	private String name;
	private String email;
	private String password;
	private String contactNo;
	private String address;

	public RegisterUserRequest() {
	}
}
