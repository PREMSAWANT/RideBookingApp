package com.ride.RideBookingApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Vehicle;
import com.ride.RideBookingApp.enums.VehicleType;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	/* fetch vehicle by vehicle number */
	Optional<Vehicle> findByVehicleNo(String vehicleNo);

	/* exists by vehicle number */
	boolean existsByVehicleNo(String vehicleNo);

	/* fetch vehicles by type */
	List<Vehicle> findByVehicleType(VehicleType vehicleType);

}

