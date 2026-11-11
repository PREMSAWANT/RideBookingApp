package com.ride.RideBookingApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Driver;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.VehicleType;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

	/* fetch driver by email */
	Optional<Driver> findByEmail(String email);

	/* exists by email */
	boolean existsByEmail(String email);

	/* fetch drivers by status */
	List<Driver> findByDriverStatus(DriverStatus driverStatus);

	/* count drivers by status */
	long countByDriverStatus(DriverStatus driverStatus);

	/* fetch drivers by status and vehicle type */
	List<Driver> findByDriverStatusAndVehicleVehicleType(DriverStatus driverStatus, VehicleType vehicleType);

}

