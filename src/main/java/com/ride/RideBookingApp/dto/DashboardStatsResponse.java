package com.ride.RideBookingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsResponse {

	private long totalUsers;
	private long totalDrivers;
	private long availableDrivers;
	private long busyDrivers;
	private long totalVehicles;
	private long totalRides;
	private long activeRides;
	private long completedRides;
	private double totalRevenue;

	public DashboardStatsResponse() {
	}
}

