package com.ride.RideBookingApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dao.VehicleDao;
import com.ride.RideBookingApp.dto.BookRideRequest;
import com.ride.RideBookingApp.dto.RegisterDriverRequest;
import com.ride.RideBookingApp.dto.RegisterDriverResponse;
import com.ride.RideBookingApp.dto.RegisterUserRequest;
import com.ride.RideBookingApp.dto.RegisterVehicleRequest;
import com.ride.RideBookingApp.dto.RegisterVehicleResponse;
import com.ride.RideBookingApp.dto.WalletRechargeRequest;
import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.enums.VehicleType;
import com.ride.RideBookingApp.service.AuthService;
import com.ride.RideBookingApp.service.DriverService;
import com.ride.RideBookingApp.service.RideService;
import com.ride.RideBookingApp.service.UserService;
import com.ride.RideBookingApp.service.VehicleService;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private VehicleDao vehicleDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;

	@Autowired
	private RideService rideService;

	@Override
	public void run(String... args) throws Exception {
		if (vehicleDao.countVehicles() > 0) {
			System.out.println("RideBookingApp database already contains data. Skipping initial seeding.");
			return;
		}

		System.out.println("Initializing sample RideBookingApp data with clean DTOs...");

		// 1. Seed Vehicles (vehicleNo, vehicleType)
		RegisterVehicleResponse vSedan = vehicleService.registerVehicle(new RegisterVehicleRequest("MH12AB1234", VehicleType.SEDAN));
		RegisterVehicleResponse vXuv = vehicleService.registerVehicle(new RegisterVehicleRequest("MH14CD5678", VehicleType.XUV));
		RegisterVehicleResponse vAuto = vehicleService.registerVehicle(new RegisterVehicleRequest("MH12EF9012", VehicleType.AUTO));
		RegisterVehicleResponse vBike = vehicleService.registerVehicle(new RegisterVehicleRequest("MH12GH3456", VehicleType.BIKE));
		RegisterVehicleResponse vMini = vehicleService.registerVehicle(new RegisterVehicleRequest("MH12JK7890", VehicleType.MINI));

		// 2. Seed Drivers (name, email, contactNo, vehicleId)
		RegisterDriverResponse d1 = driverService.registerDriver(new RegisterDriverRequest("Rajesh Kumar", "rajesh.driver@example.com", "9876543210", vSedan.getId()));
		RegisterDriverResponse d2 = driverService.registerDriver(new RegisterDriverRequest("Amit Sharma", "amit.driver@example.com", "9823456781", vXuv.getId()));
		RegisterDriverResponse d3 = driverService.registerDriver(new RegisterDriverRequest("Suresh Patil", "suresh.driver@example.com", "9812345672", vAuto.getId()));
		RegisterDriverResponse d4 = driverService.registerDriver(new RegisterDriverRequest("Vikram Singh", "vikram.driver@example.com", "9898765431", vBike.getId()));

		// 3. Seed Users (name, email, password, contactNo, address)
		authService.registerUser(new RegisterUserRequest("Prem Sawant", "premsawant600@gmail.com", "Pass@123", "9988776655", "Pune, Maharashtra"));
		authService.registerUser(new RegisterUserRequest("Rohan Joshi", "rohan.joshi@example.com", "Pass@123", "9123456780", "Mumbai, Maharashtra"));

		// 4. Fund Users' Wallets via clean WalletRechargeRequest
		User u1 = userDao.findByEmail("premsawant600@gmail.com").orElse(null);
		User u2 = userDao.findByEmail("rohan.joshi@example.com").orElse(null);

		if (u1 != null) {
			u1.setEmailVerified(true);
			userDao.saveUser(u1);
			userService.rechargeWallet(new WalletRechargeRequest(u1.getId(), 1500.0, TransactionType.UPI));
		}

		if (u2 != null) {
			u2.setEmailVerified(true);
			userDao.saveUser(u2);
			userService.rechargeWallet(new WalletRechargeRequest(u2.getId(), 800.0, TransactionType.UPI));
		}

		// 5. Seed a sample booked Ride
		if (u1 != null && d1 != null) {
			try {
				rideService.bookRide(new BookRideRequest(u1.getId(), d1.getId(), "Pune Railway Station", "Hinjewadi Tech Park Phase 1", 350.0));
				System.out.println("Sample ride booked successfully!");
			} catch (Exception ex) {
				System.out.println("Could not create initial sample ride: " + ex.getMessage());
			}
		}

		System.out.println("RideBookingApp data initialized successfully!");
	}

}
