package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterDriverRequest {

	private String name;
	private String email;
	private String contactNo;
	private Long vehicleId;

	public RegisterDriverRequest() {
	}
}
