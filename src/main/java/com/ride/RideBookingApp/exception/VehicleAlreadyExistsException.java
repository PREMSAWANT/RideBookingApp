package com.ride.RideBookingApp.exception;

public class VehicleAlreadyExistsException extends RuntimeException {
	public VehicleAlreadyExistsException(String message) {
		super(message);
	}
}

