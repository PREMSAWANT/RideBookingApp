package com.ride.RideBookingApp.dto;

import java.time.LocalDateTime;

import com.ride.RideBookingApp.enums.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideResponse {

	private String message;
	private Long id;
	private String source;
	private String destination;
	private RideStatus rideStatus;
	private double fare;
	private LocalDateTime bookingTime;
	private String userName;
	private String driverName;
	private String vehicleNo;

	public RideResponse() {
	}
}
