package com.ride.RideBookingApp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.enums.RideStatus;
import com.ride.RideBookingApp.repository.RideRepository;

@Repository
public class RideDao {

	@Autowired
	private RideRepository rideRepository;

	/* 1. save ride --> save(Ride ride) */
	public Ride saveRide(Ride ride) {
		return rideRepository.save(ride);
	}

	/* 2. save all rides --> saveAll(List<Ride> rides) */
	public List<Ride> saveAllRides(List<Ride> rides) {
		return rideRepository.saveAll(rides);
	}

	/* 3. fetch ride by id --> findById(Long id) */
	public Optional<Ride> fetchById(Long id) {
		return rideRepository.findById(id);
	}

	/* 4. fetch all rides --> findAll() */
	public List<Ride> fetchAllRides() {
		return rideRepository.findAll();
	}

	/* 5. fetch rides by user id */
	public List<Ride> findByUserId(Long userId) {
		return rideRepository.findByUserId(userId);
	}

	/* 6. fetch rides by driver id */
	public List<Ride> findByDriverId(Long driverId) {
		return rideRepository.findByDriverId(driverId);
	}

	/* 7. fetch rides by ride status */
	public List<Ride> findByRideStatus(RideStatus status) {
		return rideRepository.findByRideStatus(status);
	}

	/* 8. fetch active rides by user id */
	public List<Ride> findActiveRidesByUserId(Long userId) {
		return rideRepository.findByUserIdAndRideStatusIn(userId, List.of(RideStatus.ACCEPTED, RideStatus.ONGOING));
	}

	/* 9. fetch active rides by driver id */
	public List<Ride> findActiveRidesByDriverId(Long driverId) {
		return rideRepository.findByDriverIdAndRideStatusIn(driverId, List.of(RideStatus.ACCEPTED, RideStatus.ONGOING));
	}

	/* 8. update ride */
	public Ride updateRide(Long id, Ride newRide) {
		Optional<Ride> byId = rideRepository.findById(id);
		if (byId.isPresent()) {
			Ride existingRide = byId.get();
			Ride finalRide = new Ride();

			finalRide.setId(existingRide.getId());

			// Source check
			if (newRide.getSource() != null && !newRide.getSource().trim().isEmpty()
					&& !newRide.getSource().equalsIgnoreCase("string")) {
				finalRide.setSource(newRide.getSource());
			} else {
				finalRide.setSource(existingRide.getSource());
			}

			// Destination check
			if (newRide.getDestination() != null && !newRide.getDestination().trim().isEmpty()
					&& !newRide.getDestination().equalsIgnoreCase("string")) {
				finalRide.setDestination(newRide.getDestination());
			} else {
				finalRide.setDestination(existingRide.getDestination());
			}

			// Fare check
			if (newRide.getFare() > 0) {
				finalRide.setFare(newRide.getFare());
			} else {
				finalRide.setFare(existingRide.getFare());
			}

			// Ride status
			if (newRide.getRideStatus() != null) {
				finalRide.setRideStatus(newRide.getRideStatus());
			} else {
				finalRide.setRideStatus(existingRide.getRideStatus());
			}

			// User & Driver
			if (newRide.getUser() != null) {
				finalRide.setUser(newRide.getUser());
			} else {
				finalRide.setUser(existingRide.getUser());
			}

			if (newRide.getDriver() != null) {
				finalRide.setDriver(newRide.getDriver());
			} else {
				finalRide.setDriver(existingRide.getDriver());
			}

			if (newRide.getBookingTime() != null) {
				finalRide.setBookingTime(newRide.getBookingTime());
			} else {
				finalRide.setBookingTime(existingRide.getBookingTime());
			}

			return rideRepository.save(finalRide);
		}
		return null;
	}

	/* 9. delete ride by id */
	public Optional<Ride> deleteRide(Long id) {
		Optional<Ride> byId = rideRepository.findById(id);
		if (byId.isPresent()) {
			rideRepository.deleteById(id);
			return byId;
		}
		return Optional.empty();
	}

	/* 10. count total rides */
	public long countRides() {
		return rideRepository.count();
	}

	/* 11. count rides by status */
	public long countByRideStatus(RideStatus status) {
		return rideRepository.countByRideStatus(status);
	}

}

