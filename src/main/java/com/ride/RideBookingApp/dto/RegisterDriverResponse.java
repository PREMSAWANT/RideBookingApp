package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterDriverResponse {

	private String message;
	private Long id;
	private String name;
	private String email;
	private String contactNo;
	private DriverStatus driverStatus;
	private String vehicleNo;
	private VehicleType vehicleType;

	public RegisterDriverResponse() {
	}
}
