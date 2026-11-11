package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

	private String message;
	private Long id;
	private String name;
	private String email;
	private String contactNo;
	private double walletBalance;
	private boolean isEmailVerified;
	private String address;

	public UserResponse() {
	}
}

