package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterVehicleResponse {

	private String message;
	private Long id;
	private String vehicleNo;
	private VehicleType vehicleType;
	private String driverName;

	public RegisterVehicleResponse() {
	}
}
