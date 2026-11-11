package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FareEstimateResponse {

	private String message;
	private String source;
	private String destination;
	private VehicleType vehicleType;
	private double distanceInKm;
	private double baseFare;
	private double perKmRate;
	private double estimatedFare;

	public FareEstimateResponse() {
	}
}
