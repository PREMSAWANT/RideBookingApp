package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterVehicleRequest {

	private String vehicleNo;
	private VehicleType vehicleType;

	public RegisterVehicleRequest() {
	}
}

