package com.ride.RideBookingApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride.RideBookingApp.dto.DashboardStatsResponse;
import com.ride.RideBookingApp.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	/* Get Dashboard Summary Stats */
	@GetMapping("/stats")
	public ResponseEntity<DashboardStatsResponse> getStats() {
		DashboardStatsResponse response = dashboardService.getDashboardStats();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}

