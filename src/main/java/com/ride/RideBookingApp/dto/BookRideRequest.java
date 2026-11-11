package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookRideRequest {

	private Long userId;
	private Long driverId;
	private String source;
	private String destination;
	private double fare;

	public BookRideRequest() {
	}
}

