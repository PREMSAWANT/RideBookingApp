package com.ride.RideBookingApp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.DriverDao;
import com.ride.RideBookingApp.dao.RideDao;
import com.ride.RideBookingApp.dao.TransactionDao;
import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dto.BookRideRequest;
import com.ride.RideBookingApp.dto.FareEstimateResponse;
import com.ride.RideBookingApp.dto.RideResponse;
import com.ride.RideBookingApp.dto.UpdateRideStatusRequest;
import com.ride.RideBookingApp.emailConfig.EmailService;
import com.ride.RideBookingApp.entity.Driver;
import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.entity.Transaction;
import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.DriverStatus;
import com.ride.RideBookingApp.enums.RideStatus;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.exception.ActiveRideExistsException;
import com.ride.RideBookingApp.exception.DriverNotAvailableException;
import com.ride.RideBookingApp.exception.DriverNotFoundException;
import com.ride.RideBookingApp.exception.InsufficientWalletBalanceException;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.InvalidRideStatusTransitionException;
import com.ride.RideBookingApp.exception.RideNotFoundException;
import com.ride.RideBookingApp.exception.UserNotFoundException;

@Service
public class RideService {

	@Autowired
	private RideDao rideDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private DriverDao driverDao;

	@Autowired
	private TransactionDao transactionDao;

	@Autowired
	private EmailService emailService;

	/* 1. Book a new Ride with Scenario-based Validation */
	public RideResponse bookRide(BookRideRequest request) {

		// Scenario: Validation of locations
		if (request.getSource() == null || request.getSource().trim().isEmpty() || request.getSource().equalsIgnoreCase("string")) {
			throw new InvalidInputException("Pickup source location cannot be empty.");
		}
		if (request.getDestination() == null || request.getDestination().trim().isEmpty() || request.getDestination().equalsIgnoreCase("string")) {
			throw new InvalidInputException("Destination location cannot be empty.");
		}

		// Scenario: Source and Destination are identical
		if (request.getSource().trim().equalsIgnoreCase(request.getDestination().trim())) {
			throw new InvalidInputException("Pickup location and destination cannot be identical ('" + request.getSource().trim() + "').");
		}

		// Scenario: Minimum fare requirement
		if (request.getFare() < 50.0) {
			throw new InvalidInputException("Fare cannot be less than the minimum base fare of ₹50.00. Provided: ₹" + request.getFare());
		}

		User user = userDao.fetchById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

		// Scenario: Email verification guard
		if (!user.isEmailVerified()) {
			throw new InvalidInputException("Email is not verified for user: " + user.getEmail()
					+ ". Please verify your email via OTP before booking a ride.");
		}

		// Scenario: User already has an active ride in progress
		List<Ride> activeUserRides = rideDao.findActiveRidesByUserId(user.getId());
		if (!activeUserRides.isEmpty()) {
			Ride active = activeUserRides.get(0);
			throw new ActiveRideExistsException("User '" + user.getName() + "' already has an active ride in progress (Ride #"
					+ active.getId() + " - " + active.getRideStatus() + "). Please complete or cancel your current trip before booking a new one.");
		}

		// Scenario: Insufficient wallet balance with detailed required amount
		if (user.getWalletBalance() < request.getFare()) {
			double deficit = request.getFare() - user.getWalletBalance();
			throw new InsufficientWalletBalanceException("Insufficient wallet balance: current balance is ₹"
					+ String.format("%.2f", user.getWalletBalance()) + ", but required fare is ₹"
					+ String.format("%.2f", request.getFare()) + ". Please recharge at least ₹"
					+ String.format("%.2f", deficit) + " to book this ride.");
		}

		Driver driver = driverDao.fetchById(request.getDriverId())
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + request.getDriverId()));

		// Scenario: Driver has no vehicle assigned
		if (driver.getVehicle() == null) {
			throw new InvalidInputException("Driver '" + driver.getName()
					+ "' does not have a vehicle assigned. Only drivers with active vehicles can accept rides.");
		}

		// Scenario: Driver is not AVAILABLE
		if (driver.getDriverStatus() != DriverStatus.AVAILABLE) {
			throw new DriverNotAvailableException("Driver '" + driver.getName() + "' is currently "
					+ driver.getDriverStatus() + ". Please choose an available driver.");
		}

		// Scenario: Driver already has an active ride
		List<Ride> activeDriverRides = rideDao.findActiveRidesByDriverId(driver.getId());
		if (!activeDriverRides.isEmpty()) {
			throw new ActiveRideExistsException("Driver '" + driver.getName() + "' is currently on an active trip (Ride #"
					+ activeDriverRides.get(0).getId() + ").");
		}

		// Create Ride
		Ride ride = new Ride();
		ride.setUser(user);
		ride.setDriver(driver);
		ride.setSource(request.getSource().trim());
		ride.setDestination(request.getDestination().trim());
		ride.setFare(request.getFare());
		ride.setRideStatus(RideStatus.ACCEPTED);
		ride.setBookingTime(LocalDateTime.now());

		Ride savedRide = rideDao.saveRide(ride);

		// Transition driver to BUSY
		driver.setDriverStatus(DriverStatus.BUSY);
		driverDao.saveDriver(driver);

		// Send booking confirmation email
		String emailBody = "Hello " + user.getName() + ",\n\n"
				+ "Your cab booking has been ACCEPTED!\n"
				+ "Trip ID: #" + savedRide.getId() + "\n"
				+ "Driver: " + driver.getName() + " (" + driver.getContactNo() + ")\n"
				+ "Vehicle: " + driver.getVehicle().getVehicleNo() + " (" + driver.getVehicle().getVehicleType() + ")\n"
				+ "Route: " + ride.getSource() + " -> " + ride.getDestination() + "\n"
				+ "Estimated Fare: ₹" + String.format("%.2f", ride.getFare()) + "\n\n"
				+ "Have a safe and comfortable ride with RideBookingApp!";
		emailService.sendRideReceipt(user.getEmail(), emailBody);

		return mapToRideResponse(savedRide, "Ride booked and accepted by " + driver.getName() + " (" + driver.getVehicle().getVehicleNo() + ")");
	}

	/* 2. Update Ride Status (ACCEPTED -> ONGOING -> COMPLETED / REJECTED) */
	public RideResponse updateRideStatus(Long rideId, UpdateRideStatusRequest request) {
		Ride ride = rideDao.fetchById(rideId)
				.orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + rideId));

		RideStatus currentStatus = ride.getRideStatus();
		RideStatus newStatus = request.getRideStatus();

		if (newStatus == null) {
			throw new InvalidInputException("New ride status cannot be null.");
		}

		// Scenario: Attempt to modify finalized rides
		if (currentStatus == RideStatus.COMPLETED) {
			throw new InvalidRideStatusTransitionException("Ride #" + rideId + " is already COMPLETED and its status cannot be modified.");
		}
		if (currentStatus == RideStatus.REJECTED) {
			throw new InvalidRideStatusTransitionException("Ride #" + rideId + " is already CANCELLED/REJECTED and cannot be modified.");
		}

		// Scenario: Start Trip (ACCEPTED -> ONGOING)
		if (newStatus == RideStatus.ONGOING) {
			if (currentStatus != RideStatus.ACCEPTED) {
				throw new InvalidRideStatusTransitionException("Ride #" + rideId + " cannot start. Only rides with 'ACCEPTED' status can transition to 'ONGOING'. Current status: " + currentStatus);
			}
			ride.setRideStatus(RideStatus.ONGOING);
		}
		// Scenario: Complete Trip (ONGOING or ACCEPTED -> COMPLETED)
		else if (newStatus == RideStatus.COMPLETED) {
			if (currentStatus != RideStatus.ONGOING && currentStatus != RideStatus.ACCEPTED) {
				throw new InvalidRideStatusTransitionException("Ride #" + rideId + " must be ONGOING or ACCEPTED to complete. Current status: " + currentStatus);
			}

			User user = ride.getUser();
			// Scenario: Re-verify wallet balance at trip completion
			if (user.getWalletBalance() < ride.getFare()) {
				throw new InsufficientWalletBalanceException("User has insufficient wallet balance (₹"
						+ String.format("%.2f", user.getWalletBalance()) + ") to settle fare (₹"
						+ String.format("%.2f", ride.getFare()) + "). Please recharge wallet to complete trip.");
			}

			user.setWalletBalance(user.getWalletBalance() - ride.getFare());
			userDao.saveUser(user);

			// Record Financial Transaction
			Transaction transaction = new Transaction();
			transaction.setUser(user);
			transaction.setAmount(ride.getFare());
			transaction.setTransactionType(TransactionType.UPI);
			transaction.setDescription("Fare payment for Trip #" + ride.getId() + " (" + ride.getSource() + " to " + ride.getDestination() + ")");
			transaction.setTransactionTime(LocalDateTime.now());
			transactionDao.saveTransaction(transaction);

			// Free driver back to AVAILABLE
			Driver driver = ride.getDriver();
			if (driver != null) {
				driver.setDriverStatus(DriverStatus.AVAILABLE);
				driverDao.saveDriver(driver);
			}

			ride.setRideStatus(RideStatus.COMPLETED);

			// Send Trip Completion Receipt
			String receipt = "Hello " + user.getName() + ",\n\n"
					+ "Your ride #" + ride.getId() + " has been COMPLETED successfully.\n\n"
					+ "=== TRIP RECEIPT ===\n"
					+ "Route: " + ride.getSource() + " to " + ride.getDestination() + "\n"
					+ "Fare Deducted: ₹" + String.format("%.2f", ride.getFare()) + "\n"
					+ "Remaining Wallet Balance: ₹" + String.format("%.2f", user.getWalletBalance()) + "\n"
					+ "Driver: " + (driver != null ? driver.getName() : "N/A") + "\n\n"
					+ "Thank you for riding with RideBookingApp!";
			emailService.sendRideReceipt(user.getEmail(), receipt);

		}
		// Scenario: Cancel Trip (-> REJECTED)
		else if (newStatus == RideStatus.REJECTED) {
			Driver driver = ride.getDriver();
			if (driver != null) {
				driver.setDriverStatus(DriverStatus.AVAILABLE);
				driverDao.saveDriver(driver);
			}
			ride.setRideStatus(RideStatus.REJECTED);
		} else {
			ride.setRideStatus(newStatus);
		}

		Ride updatedRide = rideDao.saveRide(ride);
		return mapToRideResponse(updatedRide, "Ride #" + rideId + " status updated to " + updatedRide.getRideStatus());
	}

	/* 3. Cancel Ride */
	public RideResponse cancelRide(Long rideId) {
		Ride ride = rideDao.fetchById(rideId)
				.orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + rideId));

		if (ride.getRideStatus() == RideStatus.COMPLETED) {
			throw new InvalidRideStatusTransitionException("Cannot cancel Ride #" + rideId + " because it is already COMPLETED.");
		}
		if (ride.getRideStatus() == RideStatus.REJECTED) {
			throw new InvalidRideStatusTransitionException("Ride #" + rideId + " is already cancelled.");
		}

		UpdateRideStatusRequest request = new UpdateRideStatusRequest();
		request.setRideStatus(RideStatus.REJECTED);
		return updateRideStatus(rideId, request);
	}

	/* 4. Fare Estimation based on Vehicle Type and Distance */
	public FareEstimateResponse estimateFare(String source, String destination, VehicleType vehicleType, double distanceInKm) {
		if (distanceInKm <= 0) {
			throw new InvalidInputException("Distance must be greater than 0 km.");
		}

		double baseFare;
		double perKmRate;

		switch (vehicleType) {
			case BIKE:
				baseFare = 25.0;
				perKmRate = 8.0;
				break;
			case AUTO:
				baseFare = 35.0;
				perKmRate = 12.0;
				break;
			case MINI:
				baseFare = 60.0;
				perKmRate = 16.0;
				break;
			case SEDAN:
				baseFare = 90.0;
				perKmRate = 22.0;
				break;
			case XUV:
				baseFare = 130.0;
				perKmRate = 28.0;
				break;
			default:
				baseFare = 50.0;
				perKmRate = 15.0;
				break;
		}

		double estimatedFare = Math.round((baseFare + (distanceInKm * perKmRate)) * 100.0) / 100.0;

		return new FareEstimateResponse("Fare calculated successfully", source, destination, vehicleType,
				distanceInKm, baseFare, perKmRate, estimatedFare);
	}

	/* 5. Fetch Ride by ID */
	public RideResponse fetchRideById(Long id) {
		Ride ride = rideDao.fetchById(id)
				.orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
		return mapToRideResponse(ride, "Ride fetched successfully");
	}

	/* 6. Fetch All Rides */
	public List<RideResponse> fetchAllRides() {
		List<Ride> rides = rideDao.fetchAllRides();
		List<RideResponse> responses = new ArrayList<>();
		for (Ride ride : rides) {
			responses.add(mapToRideResponse(ride, "Ride fetched successfully"));
		}
		return responses;
	}

	/* 7. Fetch Rides by User ID */
	public List<RideResponse> fetchRidesByUserId(Long userId) {
		userDao.fetchById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
		List<Ride> rides = rideDao.findByUserId(userId);
		List<RideResponse> responses = new ArrayList<>();
		for (Ride ride : rides) {
			responses.add(mapToRideResponse(ride, "Ride fetched successfully"));
		}
		return responses;
	}

	/* 8. Fetch Rides by Driver ID */
	public List<RideResponse> fetchRidesByDriverId(Long driverId) {
		driverDao.fetchById(driverId)
				.orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));
		List<Ride> rides = rideDao.findByDriverId(driverId);
		List<RideResponse> responses = new ArrayList<>();
		for (Ride ride : rides) {
			responses.add(mapToRideResponse(ride, "Ride fetched successfully"));
		}
		return responses;
	}

	/* 9. Fetch Rides by Status */
	public List<RideResponse> fetchRidesByStatus(RideStatus status) {
		List<Ride> rides = rideDao.findByRideStatus(status);
		List<RideResponse> responses = new ArrayList<>();
		for (Ride ride : rides) {
			responses.add(mapToRideResponse(ride, "Ride fetched successfully"));
		}
		return responses;
	}

	/* Helper method to map Ride to clean RideResponse */
	public RideResponse mapToRideResponse(Ride ride, String message) {
		String userName = ride.getUser() != null ? ride.getUser().getName() : null;
		String driverName = ride.getDriver() != null ? ride.getDriver().getName() : null;
		String vehicleNo = (ride.getDriver() != null && ride.getDriver().getVehicle() != null)
				? ride.getDriver().getVehicle().getVehicleNo() : "N/A";

		return new RideResponse(message, ride.getId(), ride.getSource(), ride.getDestination(),
				ride.getRideStatus(), ride.getFare(), ride.getBookingTime(), userName,
				driverName, vehicleNo);
	}

}
