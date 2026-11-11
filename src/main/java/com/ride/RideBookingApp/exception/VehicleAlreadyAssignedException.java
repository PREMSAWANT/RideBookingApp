package com.ride.RideBookingApp.exception;

public class VehicleAlreadyAssignedException extends RuntimeException {
	public VehicleAlreadyAssignedException(String message) {
		super(message);
	}
}

