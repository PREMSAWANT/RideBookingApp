package com.ride.RideBookingApp.exception;

public class InsufficientWalletBalanceException extends RuntimeException {
	public InsufficientWalletBalanceException(String message) {
		super(message);
	}
}

