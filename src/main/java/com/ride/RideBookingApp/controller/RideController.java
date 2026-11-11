package com.ride.RideBookingApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride.RideBookingApp.dto.BookRideRequest;
import com.ride.RideBookingApp.dto.FareEstimateResponse;
import com.ride.RideBookingApp.dto.RideResponse;
import com.ride.RideBookingApp.dto.UpdateRideStatusRequest;
import com.ride.RideBookingApp.enums.RideStatus;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.service.RideService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping({"/api/ride", "/api/rides"})
public class RideController {

	@Autowired
	private RideService rideService;

	/* 1. Book a new Ride */
	@PostMapping("/book")
	public ResponseEntity<RideResponse> bookRide(@RequestBody BookRideRequest request) {
		RideResponse response = rideService.bookRide(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* 2. Estimate Fare based on Vehicle Type & Distance */
	@GetMapping("/estimateFare")
	public ResponseEntity<FareEstimateResponse> estimateFare(
			@RequestParam String source,
			@RequestParam String destination,
			@RequestParam VehicleType vehicleType,
			@RequestParam double distanceInKm) {
		FareEstimateResponse response = rideService.estimateFare(source, destination, vehicleType, distanceInKm);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 2. Update Ride Status (ACCEPTED -> ONGOING -> COMPLETED / REJECTED) */
	@PutMapping("/updateStatus/{rideId}")
	public ResponseEntity<RideResponse> updateRideStatus(@PathVariable Long rideId, @RequestBody UpdateRideStatusRequest request) {
		RideResponse response = rideService.updateRideStatus(rideId, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 3. Cancel Ride */
	@PutMapping("/cancel/{rideId}")
	public ResponseEntity<RideResponse> cancelRide(@PathVariable Long rideId) {
		RideResponse response = rideService.cancelRide(rideId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 4. Fetch Ride by ID */
	@GetMapping("/fetchById/{id}")
	public ResponseEntity<RideResponse> fetchById(@PathVariable Long id) {
		RideResponse response = rideService.fetchRideById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 5. Fetch All Rides */
	@GetMapping("/getAll")
	public ResponseEntity<List<RideResponse>> fetchAllRides() {
		List<RideResponse> responses = rideService.fetchAllRides();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 6. Fetch Rides by User ID */
	@GetMapping("/byUser/{userId}")
	public ResponseEntity<List<RideResponse>> fetchByUserId(@PathVariable Long userId) {
		List<RideResponse> responses = rideService.fetchRidesByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 7. Fetch Rides by Driver ID */
	@GetMapping("/byDriver/{driverId}")
	public ResponseEntity<List<RideResponse>> fetchByDriverId(@PathVariable Long driverId) {
		List<RideResponse> responses = rideService.fetchRidesByDriverId(driverId);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 8. Fetch Rides by Status */
	@GetMapping("/byStatus/{status}")
	public ResponseEntity<List<RideResponse>> fetchByStatus(@PathVariable RideStatus status) {
		List<RideResponse> responses = rideService.fetchRidesByStatus(status);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

}

