package com.ride.RideBookingApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride.RideBookingApp.dto.RegisterVehicleRequest;
import com.ride.RideBookingApp.dto.RegisterVehicleResponse;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.service.VehicleService;

@RestController
@RequestMapping({"/api/vehicle", "/api/vehicles"})
public class VehicleController {

	@Autowired
	private VehicleService vehicleService;

	/* 1. Register Vehicle */
	@PostMapping("/save")
	public ResponseEntity<RegisterVehicleResponse> registerVehicle(@RequestBody RegisterVehicleRequest request) {
		RegisterVehicleResponse response = vehicleService.registerVehicle(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* 2. Fetch Vehicle by ID */
	@GetMapping("/fetchById/{id}")
	public ResponseEntity<RegisterVehicleResponse> fetchById(@PathVariable Long id) {
		RegisterVehicleResponse response = vehicleService.fetchVehicleById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 3. Fetch All Vehicles */
	@GetMapping("/getAll")
	public ResponseEntity<List<RegisterVehicleResponse>> fetchAllVehicles() {
		List<RegisterVehicleResponse> responses = vehicleService.fetchAllVehicles();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 4. Fetch Vehicles by Type */
	@GetMapping("/byType/{type}")
	public ResponseEntity<List<RegisterVehicleResponse>> fetchByType(@PathVariable VehicleType type) {
		List<RegisterVehicleResponse> responses = vehicleService.fetchVehiclesByType(type);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 5. Update Vehicle */
	@PutMapping("/update/{id}")
	public ResponseEntity<RegisterVehicleResponse> updateVehicle(@PathVariable Long id, @RequestBody RegisterVehicleRequest request) {
		RegisterVehicleResponse response = vehicleService.updateVehicle(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 6. Delete Vehicle */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteVehicle(@PathVariable Long id) {
		String result = vehicleService.deleteVehicle(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

}

