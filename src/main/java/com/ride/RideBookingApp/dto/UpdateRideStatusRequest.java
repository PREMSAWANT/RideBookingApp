package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRideStatusRequest {

	private RideStatus rideStatus;

	public UpdateRideStatusRequest() {
	}
}

