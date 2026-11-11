package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResendOtpRequest {

	private String email;

	public ResendOtpRequest() {
	}
}

