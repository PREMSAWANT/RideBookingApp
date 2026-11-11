package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResendOtpResponse {

	private String message;
	private String email;

	public ResendOtpResponse() {
	}
}
