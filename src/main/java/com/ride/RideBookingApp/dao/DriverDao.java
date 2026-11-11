package com.ride.RideBookingApp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Driver;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.repository.DriverRepository;

@Repository
public class DriverDao {

	@Autowired
	private DriverRepository driverRepository;

	/* 1. save driver --> save(Driver driver) */
	public Driver saveDriver(Driver driver) {
		return driverRepository.save(driver);
	}

	/* 2. save all drivers --> saveAll(List<Driver> drivers) */
	public List<Driver> saveAllDrivers(List<Driver> drivers) {
		return driverRepository.saveAll(drivers);
	}

	/* 3. fetch driver by id --> findById(Long id) */
	public Optional<Driver> fetchById(Long id) {
		return driverRepository.findById(id);
	}

	/* 4. find by email --> findByEmail(String email) */
	public Optional<Driver> findByEmail(String email) {
		return driverRepository.findByEmail(email);
	}

	/* 5. exists by email --> existsByEmail(String email) */
	public boolean existsByEmail(String email) {
		return driverRepository.existsByEmail(email);
	}

	/* 6. fetch all drivers --> findAll() */
	public List<Driver> fetchAllDrivers() {
		return driverRepository.findAll();
	}

	/* 7. fetch drivers by status --> findByDriverStatus(DriverStatus status) */
	public List<Driver> findByStatus(DriverStatus status) {
		return driverRepository.findByDriverStatus(status);
	}

	/* 8. fetch available drivers by vehicle type */
	public List<Driver> findAvailableByVehicleType(VehicleType vehicleType) {
		return driverRepository.findByDriverStatusAndVehicleVehicleType(DriverStatus.AVAILABLE, vehicleType);
	}

	/* 8. update driver */
	public Driver updateDriver(Long id, Driver newDriver) {
		Optional<Driver> byId = driverRepository.findById(id);
		if (byId.isPresent()) {
			Driver existingDriver = byId.get();
			Driver finalDriver = new Driver();

			finalDriver.setId(existingDriver.getId());

			// Name check
			if (newDriver.getName() != null && !newDriver.getName().trim().isEmpty()
					&& !newDriver.getName().equalsIgnoreCase("string")) {
				finalDriver.setName(newDriver.getName());
			} else {
				finalDriver.setName(existingDriver.getName());
			}

			// Email check
			if (newDriver.getEmail() != null && !newDriver.getEmail().trim().isEmpty()
					&& !newDriver.getEmail().equalsIgnoreCase("string")) {
				finalDriver.setEmail(newDriver.getEmail());
			} else {
				finalDriver.setEmail(existingDriver.getEmail());
			}

			// Contact number check
			if (newDriver.getContactNo() != null && !newDriver.getContactNo().trim().isEmpty()
					&& !newDriver.getContactNo().equalsIgnoreCase("string")) {
				finalDriver.setContactNo(newDriver.getContactNo());
			} else {
				finalDriver.setContactNo(existingDriver.getContactNo());
			}

			// Driver status
			if (newDriver.getDriverStatus() != null) {
				finalDriver.setDriverStatus(newDriver.getDriverStatus());
			} else {
				finalDriver.setDriverStatus(existingDriver.getDriverStatus());
			}

			// Vehicle
			if (newDriver.getVehicle() != null) {
				finalDriver.setVehicle(newDriver.getVehicle());
			} else {
				finalDriver.setVehicle(existingDriver.getVehicle());
			}

			finalDriver.setRides(existingDriver.getRides());

			return driverRepository.save(finalDriver);
		}
		return null;
	}

	/* 9. delete driver by id */
	public Optional<Driver> deleteDriver(Long id) {
		Optional<Driver> byId = driverRepository.findById(id);
		if (byId.isPresent()) {
			driverRepository.deleteById(id);
			return byId;
		}
		return Optional.empty();
	}

	/* 10. count total drivers */
	public long countDrivers() {
		return driverRepository.count();
	}

	/* 11. count drivers by status */
	public long countByStatus(DriverStatus status) {
		return driverRepository.countByDriverStatus(status);
	}

}

