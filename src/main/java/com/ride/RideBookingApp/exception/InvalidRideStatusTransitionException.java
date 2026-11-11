package com.ride.RideBookingApp.exception;

public class InvalidRideStatusTransitionException extends RuntimeException {
	public InvalidRideStatusTransitionException(String message) {
		super(message);
	}
}

