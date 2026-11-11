package com.ride.RideBookingApp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Vehicle;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.repository.VehicleRepository;

@Repository
public class VehicleDao {

	@Autowired
	private VehicleRepository vehicleRepository;

	/* 1. save vehicle --> save(Vehicle vehicle) */
	public Vehicle saveVehicle(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	/* 2. save all vehicles --> saveAll(List<Vehicle> vehicles) */
	public List<Vehicle> saveAllVehicles(List<Vehicle> vehicles) {
		return vehicleRepository.saveAll(vehicles);
	}

	/* 3. fetch vehicle by id --> findById(Long id) */
	public Optional<Vehicle> fetchById(Long id) {
		return vehicleRepository.findById(id);
	}

	/* 4. find by vehicle number --> findByVehicleNo(String vehicleNo) */
	public Optional<Vehicle> findByVehicleNo(String vehicleNo) {
		return vehicleRepository.findByVehicleNo(vehicleNo);
	}

	/* 5. exists by vehicle number */
	public boolean existsByVehicleNo(String vehicleNo) {
		return vehicleRepository.existsByVehicleNo(vehicleNo);
	}

	/* 6. fetch all vehicles --> findAll() */
	public List<Vehicle> fetchAllVehicles() {
		return vehicleRepository.findAll();
	}

	/* 7. fetch vehicles by type */
	public List<Vehicle> findByVehicleType(VehicleType vehicleType) {
		return vehicleRepository.findByVehicleType(vehicleType);
	}

	/* 8. update vehicle */
	public Vehicle updateVehicle(Long id, Vehicle newVehicle) {
		Optional<Vehicle> byId = vehicleRepository.findById(id);
		if (byId.isPresent()) {
			Vehicle existingVehicle = byId.get();
			Vehicle finalVehicle = new Vehicle();

			finalVehicle.setId(existingVehicle.getId());

			// Vehicle number check
			if (newVehicle.getVehicleNo() != null && !newVehicle.getVehicleNo().trim().isEmpty()
					&& !newVehicle.getVehicleNo().equalsIgnoreCase("string")) {
				finalVehicle.setVehicleNo(newVehicle.getVehicleNo());
			} else {
				finalVehicle.setVehicleNo(existingVehicle.getVehicleNo());
			}

			// Vehicle type check
			if (newVehicle.getVehicleType() != null) {
				finalVehicle.setVehicleType(newVehicle.getVehicleType());
			} else {
				finalVehicle.setVehicleType(existingVehicle.getVehicleType());
			}

			finalVehicle.setDriver(existingVehicle.getDriver());

			return vehicleRepository.save(finalVehicle);
		}
		return null;
	}

	/* 9. delete vehicle by id */
	public Optional<Vehicle> deleteVehicle(Long id) {
		Optional<Vehicle> byId = vehicleRepository.findById(id);
		if (byId.isPresent()) {
			vehicleRepository.deleteById(id);
			return byId;
		}
		return Optional.empty();
	}

	/* 10. count total vehicles */
	public long countVehicles() {
		return vehicleRepository.count();
	}

}

