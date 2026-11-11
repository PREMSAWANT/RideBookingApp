package com.ride.RideBookingApp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.RideDao;
import com.ride.RideBookingApp.dao.VehicleDao;
import com.ride.RideBookingApp.dto.RegisterVehicleRequest;
import com.ride.RideBookingApp.dto.RegisterVehicleResponse;
import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.entity.Vehicle;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.exception.ActiveRideExistsException;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.VehicleAlreadyExistsException;
import com.ride.RideBookingApp.exception.VehicleNotFoundException;

@Service
public class VehicleService {

	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private RideDao rideDao;

	/* 1. Register Vehicle */
	public RegisterVehicleResponse registerVehicle(RegisterVehicleRequest request) {
		if (request.getVehicleNo() == null || request.getVehicleNo().trim().isEmpty()
				|| request.getVehicleNo().equalsIgnoreCase("string")) {
			throw new InvalidInputException("Vehicle number cannot be empty or default string.");
		}

		String cleanNo = request.getVehicleNo().trim().toUpperCase().replaceAll("\\s+", "");
		// Standard Indian vehicle format validation (e.g. MH12AB1234 or DL1C1234)
		if (!cleanNo.matches("^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$")) {
			throw new InvalidInputException("Invalid vehicle registration number '" + request.getVehicleNo()
					+ "'. Must follow standard format (e.g. MH12AB1234 or DL01C1234).");
		}

		if (vehicleDao.existsByVehicleNo(cleanNo)) {
			throw new VehicleAlreadyExistsException("Vehicle already registered with number: " + cleanNo);
		}

		Vehicle vehicle = new Vehicle();
		vehicle.setVehicleNo(cleanNo);
		vehicle.setVehicleType(request.getVehicleType() != null ? request.getVehicleType() : VehicleType.SEDAN);

		Vehicle savedVehicle = vehicleDao.saveVehicle(vehicle);

		return mapToVehicleResponse(savedVehicle, "Vehicle registered successfully");
	}

	/* 2. Fetch Vehicle by ID */
	public RegisterVehicleResponse fetchVehicleById(Long id) {
		Vehicle vehicle = vehicleDao.fetchById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
		return mapToVehicleResponse(vehicle, "Vehicle fetched successfully");
	}

	/* 3. Fetch All Vehicles */
	public List<RegisterVehicleResponse> fetchAllVehicles() {
		List<Vehicle> vehicles = vehicleDao.fetchAllVehicles();
		List<RegisterVehicleResponse> responses = new ArrayList<>();
		for (Vehicle vehicle : vehicles) {
			responses.add(mapToVehicleResponse(vehicle, "Vehicle fetched successfully"));
		}
		return responses;
	}

	/* 4. Fetch Vehicles by Type */
	public List<RegisterVehicleResponse> fetchVehiclesByType(VehicleType type) {
		List<Vehicle> vehicles = vehicleDao.findByVehicleType(type);
		List<RegisterVehicleResponse> responses = new ArrayList<>();
		for (Vehicle vehicle : vehicles) {
			responses.add(mapToVehicleResponse(vehicle, "Vehicle fetched successfully"));
		}
		return responses;
	}

	/* 5. Update Vehicle */
	public RegisterVehicleResponse updateVehicle(Long id, RegisterVehicleRequest request) {
		Vehicle existingVehicle = vehicleDao.fetchById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

		String cleanNo = request.getVehicleNo() != null ? request.getVehicleNo().trim().toUpperCase().replaceAll("\\s+", "") : existingVehicle.getVehicleNo();
		if (!cleanNo.equalsIgnoreCase("string") && !cleanNo.matches("^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$")) {
			throw new InvalidInputException("Invalid vehicle registration number '" + request.getVehicleNo() + "'.");
		}

		Vehicle vehicleToUpdate = new Vehicle();
		vehicleToUpdate.setVehicleNo(cleanNo);
		vehicleToUpdate.setVehicleType(request.getVehicleType() != null ? request.getVehicleType() : existingVehicle.getVehicleType());

		Vehicle updatedVehicle = vehicleDao.updateVehicle(id, vehicleToUpdate);

		return mapToVehicleResponse(updatedVehicle, "Vehicle updated successfully");
	}

	/* 6. Delete Vehicle */
	public String deleteVehicle(Long id) {
		Vehicle vehicle = vehicleDao.fetchById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

		// Scenario check: check if assigned driver has active ride
		if (vehicle.getDriver() != null) {
			List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(vehicle.getDriver().getId());
			if (!activeDriverRides.isEmpty()) {
				throw new ActiveRideExistsException("Vehicle cannot be deleted because its assigned driver '"
						+ vehicle.getDriver().getName() + "' is on active trip #" + activeDriverRides.get(0).getId() + ".");
			}
		}

		vehicleDao.deleteVehicle(id);
		return "Vehicle deleted successfully with id: " + id;
	}

	/* Helper method to map Vehicle to clean RegisterVehicleResponse */
	public RegisterVehicleResponse mapToVehicleResponse(Vehicle vehicle, String message) {
		String driverName = vehicle.getDriver() != null ? vehicle.getDriver().getName() : null;

		return new RegisterVehicleResponse(message, vehicle.getId(), vehicle.getVehicleNo(),
				vehicle.getVehicleType(), driverName);
	}

}
