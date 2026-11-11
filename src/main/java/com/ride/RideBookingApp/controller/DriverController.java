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

import com.ride.RideBookingApp.dto.AssignVehicleRequest;
import com.ride.RideBookingApp.dto.RegisterDriverRequest;
import com.ride.RideBookingApp.dto.RegisterDriverResponse;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.service.DriverService;

@RestController
@RequestMapping({"/api/driver", "/api/drivers"})
public class DriverController {

	@Autowired
	private DriverService driverService;

	/* 1. Register Driver */
	@PostMapping("/save")
	public ResponseEntity<RegisterDriverResponse> registerDriver(@RequestBody RegisterDriverRequest request) {
		RegisterDriverResponse response = driverService.registerDriver(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* 2. Assign Vehicle to Driver */
	@PostMapping("/assignVehicle")
	public ResponseEntity<RegisterDriverResponse> assignVehicle(@RequestBody AssignVehicleRequest request) {
		RegisterDriverResponse response = driverService.assignVehicle(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 3. Unassign Vehicle from Driver */
	@PostMapping("/unassignVehicle/{driverId}")
	public ResponseEntity<RegisterDriverResponse> unassignVehicle(@PathVariable Long driverId) {
		RegisterDriverResponse response = driverService.unassignVehicle(driverId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 4. Fetch Available Drivers by Vehicle Type */
	@GetMapping("/availableByVehicleType/{vehicleType}")
	public ResponseEntity<List<RegisterDriverResponse>> fetchAvailableByVehicleType(@PathVariable VehicleType vehicleType) {
		List<RegisterDriverResponse> responses = driverService.fetchAvailableByVehicleType(vehicleType);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 3. Fetch Driver by ID */
	@GetMapping("/fetchById/{id}")
	public ResponseEntity<RegisterDriverResponse> fetchById(@PathVariable Long id) {
		RegisterDriverResponse response = driverService.fetchDriverById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 4. Fetch All Drivers */
	@GetMapping("/getAll")
	public ResponseEntity<List<RegisterDriverResponse>> fetchAllDrivers() {
		List<RegisterDriverResponse> responses = driverService.fetchAllDrivers();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 5. Fetch Drivers by Status */
	@GetMapping("/byStatus/{status}")
	public ResponseEntity<List<RegisterDriverResponse>> fetchByStatus(@PathVariable DriverStatus status) {
		List<RegisterDriverResponse> responses = driverService.fetchDriversByStatus(status);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 6. Update Driver Status */
	@PutMapping("/updateStatus/{id}/{status}")
	public ResponseEntity<RegisterDriverResponse> updateStatus(@PathVariable Long id, @PathVariable DriverStatus status) {
		RegisterDriverResponse response = driverService.updateDriverStatus(id, status);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 7. Update Driver */
	@PutMapping("/update/{id}")
	public ResponseEntity<RegisterDriverResponse> updateDriver(@PathVariable Long id, @RequestBody RegisterDriverRequest request) {
		RegisterDriverResponse response = driverService.updateDriver(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 8. Delete Driver */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
		String result = driverService.deleteDriver(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

}

