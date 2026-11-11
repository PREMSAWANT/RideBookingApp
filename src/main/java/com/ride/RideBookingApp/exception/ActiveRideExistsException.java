package com.ride.RideBookingApp.exception;

public class ActiveRideExistsException extends RuntimeException {
	public ActiveRideExistsException(String message) {
		super(message);
	}
}
