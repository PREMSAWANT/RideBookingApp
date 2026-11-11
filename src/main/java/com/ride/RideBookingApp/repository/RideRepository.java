package com.ride.RideBookingApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.enums.RideStatus;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

	/* fetch rides by user id */
	List<Ride> findByUserId(Long userId);

	/* fetch rides by driver id */
	List<Ride> findByDriverId(Long driverId);

	/* fetch rides by ride status */
	List<Ride> findByRideStatus(RideStatus rideStatus);

	/* count rides by ride status */
	long countByRideStatus(RideStatus rideStatus);

	/* check if user has active rides (ACCEPTED or ONGOING) */
	List<Ride> findByUserIdAndRideStatusIn(Long userId, List<RideStatus> statuses);

	/* check if driver has active rides (ACCEPTED or ONGOING) */
	List<Ride> findByDriverIdAndRideStatusIn(Long driverId, List<RideStatus> statuses);

}

