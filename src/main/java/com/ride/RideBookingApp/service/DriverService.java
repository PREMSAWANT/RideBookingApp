package com.ride.RideBookingApp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.DriverDao;
import com.ride.RideBookingApp.dao.RideDao;
import com.ride.RideBookingApp.dao.VehicleDao;
import com.ride.RideBookingApp.dto.AssignVehicleRequest;
import com.ride.RideBookingApp.dto.RegisterDriverRequest;
import com.ride.RideBookingApp.dto.RegisterDriverResponse;
import com.ride.RideBookingApp.entity.Driver;
import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.entity.Vehicle;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.exception.ActiveRideExistsException;
import com.ride.RideBookingApp.exception.DriverAlreadyExistsException;
import com.ride.RideBookingApp.exception.DriverNotFoundException;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.VehicleAlreadyAssignedException;
import com.ride.RideBookingApp.exception.VehicleNotFoundException;

@Service
public class DriverService {

	@Autowired
	private DriverDao driverDao;

	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private RideDao rideDao;

	/* 1. Register Driver */
	public RegisterDriverResponse registerDriver(RegisterDriverRequest request) {
		if (request.getEmail() == null || request.getEmail().trim().isEmpty()
				|| request.getEmail().equalsIgnoreCase("string")) {
			throw new InvalidInputException("Email cannot be empty or default string.");
		}

		if (request.getContactNo() != null && !request.getContactNo().trim().isEmpty()) {
			if (!request.getContactNo().trim().matches("^[6-9]\\d{9}$")) {
				throw new InvalidInputException("Invalid contact number '" + request.getContactNo()
						+ "'. Must be a valid 10-digit mobile number starting with 6, 7, 8, or 9.");
			}
		}

		if (driverDao.existsByEmail(request.getEmail())) {
			throw new DriverAlreadyExistsException("Driver already exists with email: " + request.getEmail());
		}

		Driver driver = new Driver();
		driver.setName(request.getName());
		driver.setEmail(request.getEmail());
		driver.setContactNo(request.getContactNo());
		driver.setDriverStatus(DriverStatus.AVAILABLE);

		if (request.getVehicleId() != null) {
			Vehicle vehicle = vehicleDao.fetchById(request.getVehicleId())
					.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + request.getVehicleId()));
			if (vehicle.getDriver() != null) {
				throw new VehicleAlreadyAssignedException("Vehicle '" + vehicle.getVehicleNo()
						+ "' is already assigned to driver: " + vehicle.getDriver().getName());
			}
			driver.setVehicle(vehicle);
		}

		Driver savedDriver = driverDao.saveDriver(driver);

		return mapToDriverResponse(savedDriver, "Driver registered successfully");
	}

	/* 2. Assign Vehicle to Driver */
	public RegisterDriverResponse assignVehicle(AssignVehicleRequest request) {
		Driver driver = driverDao.fetchById(request.getDriverId())
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + request.getDriverId()));

		// Scenario check: Cannot reassign vehicle while on active ride
		List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(driver.getId());
		if (!activeDriverRides.isEmpty()) {
			throw new ActiveRideExistsException("Cannot change vehicle while driver '" + driver.getName()
					+ "' is currently on an active trip (#" + activeDriverRides.get(0).getId() + ").");
		}

		Vehicle vehicle = vehicleDao.fetchById(request.getVehicleId())
				.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

		if (vehicle.getDriver() != null && !vehicle.getDriver().getId().equals(driver.getId())) {
			throw new VehicleAlreadyAssignedException("Vehicle " + vehicle.getVehicleNo()
					+ " is already assigned to driver: " + vehicle.getDriver().getName());
		}

		driver.setVehicle(vehicle);
		Driver updatedDriver = driverDao.saveDriver(driver);

		return mapToDriverResponse(updatedDriver, "Vehicle assigned successfully to driver " + updatedDriver.getName());
	}

	/* 3. Unassign Vehicle from Driver */
	public RegisterDriverResponse unassignVehicle(Long driverId) {
		Driver driver = driverDao.fetchById(driverId)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));

		if (driver.getVehicle() == null) {
			return mapToDriverResponse(driver, "Driver has no vehicle assigned.");
		}

		List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(driverId);
		if (!activeDriverRides.isEmpty()) {
			throw new ActiveRideExistsException("Cannot unassign vehicle while driver is currently on an active trip (#"
					+ activeDriverRides.get(0).getId() + ").");
		}

		driver.setVehicle(null);
		Driver updatedDriver = driverDao.saveDriver(driver);

		return mapToDriverResponse(updatedDriver, "Vehicle successfully unassigned from driver " + updatedDriver.getName());
	}

	/* 4. Fetch Driver by ID */
	public RegisterDriverResponse fetchDriverById(Long id) {
		Driver driver = driverDao.fetchById(id)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
		return mapToDriverResponse(driver, "Driver fetched successfully");
	}

	/* 5. Fetch All Drivers */
	public List<RegisterDriverResponse> fetchAllDrivers() {
		List<Driver> drivers = driverDao.fetchAllDrivers();
		List<RegisterDriverResponse> responses = new ArrayList<>();
		for (Driver driver : drivers) {
			responses.add(mapToDriverResponse(driver, "Driver fetched successfully"));
		}
		return responses;
	}

	/* 6. Fetch Drivers by Status */
	public List<RegisterDriverResponse> fetchDriversByStatus(DriverStatus status) {
		List<Driver> drivers = driverDao.findByStatus(status);
		List<RegisterDriverResponse> responses = new ArrayList<>();
		for (Driver driver : drivers) {
			responses.add(mapToDriverResponse(driver, "Driver fetched successfully"));
		}
		return responses;
	}

	/* 7. Fetch Available Drivers by Vehicle Type */
	public List<RegisterDriverResponse> fetchAvailableByVehicleType(VehicleType vehicleType) {
		List<Driver> drivers = driverDao.findAvailableByVehicleType(vehicleType);
		List<RegisterDriverResponse> responses = new ArrayList<>();
		for (Driver driver : drivers) {
			responses.add(mapToDriverResponse(driver, "Available driver fetched for " + vehicleType));
		}
		return responses;
	}

	/* 8. Update Driver Status with scenario check */
	public RegisterDriverResponse updateDriverStatus(Long id, DriverStatus status) {
		Driver driver = driverDao.fetchById(id)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

		// Scenario check: if driver has an active trip, cannot go OFFLINE or AVAILABLE
		List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(id);
		if (!activeDriverRides.isEmpty() && status != DriverStatus.BUSY) {
			throw new ActiveRideExistsException("Driver '" + driver.getName() + "' cannot switch to " + status
					+ " while currently engaged on active Ride #" + activeDriverRides.get(0).getId() + ".");
		}

		driver.setDriverStatus(status);
		Driver updatedDriver = driverDao.saveDriver(driver);
		return mapToDriverResponse(updatedDriver, "Driver status updated to " + status);
	}

	/* 9. Update Driver */
	public RegisterDriverResponse updateDriver(Long id, RegisterDriverRequest request) {
		driverDao.fetchById(id)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

		Driver driverToUpdate = new Driver();
		driverToUpdate.setName(request.getName());
		driverToUpdate.setEmail(request.getEmail());
		driverToUpdate.setContactNo(request.getContactNo());

		if (request.getVehicleId() != null) {
			Vehicle vehicle = vehicleDao.fetchById(request.getVehicleId())
					.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + request.getVehicleId()));
			driverToUpdate.setVehicle(vehicle);
		}

		Driver updatedDriver = driverDao.updateDriver(id, driverToUpdate);

		return mapToDriverResponse(updatedDriver, "Driver updated successfully");
	}

	/* 10. Delete Driver */
	public String deleteDriver(Long id) {
		Driver driver = driverDao.fetchById(id)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

		// Scenario check: cannot delete if active trip
		List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(id);
		if (!activeDriverRides.isEmpty()) {
			throw new ActiveRideExistsException("Driver '" + driver.getName() + "' cannot be deleted because they are assigned to active ride #"
					+ activeDriverRides.get(0).getId() + ".");
		}

		driverDao.deleteDriver(id);
		return "Driver deleted successfully with id: " + id;
	}

	/* Helper method to map Driver to clean RegisterDriverResponse */
	public RegisterDriverResponse mapToDriverResponse(Driver driver, String message) {
		String vehicleNo = driver.getVehicle() != null ? driver.getVehicle().getVehicleNo() : null;
		VehicleType vehicleType = driver.getVehicle() != null ? driver.getVehicle().getVehicleType() : null;

		return new RegisterDriverResponse(message, driver.getId(), driver.getName(), driver.getEmail(),
				driver.getContactNo(), driver.getDriverStatus(), vehicleNo, vehicleType);
	}

}
