package com.ride.RideBookingApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.DriverDao;
import com.ride.RideBookingApp.dao.RideDao;
import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dao.VehicleDao;
import com.ride.RideBookingApp.dto.DashboardStatsResponse;
import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.RideStatus;

@Service
public class DashboardService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private DriverDao driverDao;

	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private RideDao rideDao;

	/* Get Dashboard summary statistics */
	public DashboardStatsResponse getDashboardStats() {
		long totalUsers = userDao.countUsers();
		long totalDrivers = driverDao.countDrivers();
		long availableDrivers = driverDao.countByStatus(DriverStatus.AVAILABLE);
		long busyDrivers = driverDao.countByStatus(DriverStatus.BUSY);
		long totalVehicles = vehicleDao.countVehicles();
		long totalRides = rideDao.countRides();
		long acceptedRides = rideDao.countByRideStatus(RideStatus.ACCEPTED);
		long ongoingRides = rideDao.countByRideStatus(RideStatus.ONGOING);
		long completedRides = rideDao.countByRideStatus(RideStatus.COMPLETED);

		// Calculate total revenue from completed rides
		List<Ride> completedRidesList = rideDao.findByRideStatus(RideStatus.COMPLETED);
		double totalRevenue = 0.0;
		for (Ride ride : completedRidesList) {
			totalRevenue += ride.getFare();
		}

		return new DashboardStatsResponse(totalUsers, totalDrivers, availableDrivers, busyDrivers,
				totalVehicles, totalRides, (acceptedRides + ongoingRides), completedRides, totalRevenue);
	}

}

