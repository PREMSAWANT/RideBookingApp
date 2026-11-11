package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignVehicleRequest {

	private Long driverId;
	private Long vehicleId;

	public AssignVehicleRequest() {
	}
}

