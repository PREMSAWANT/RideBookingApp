package com.ride.RideBookingApp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.RideDao;
import com.ride.RideBookingApp.dao.TransactionDao;
import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dto.RegisterUserRequest;
import com.ride.RideBookingApp.dto.UserResponse;
import com.ride.RideBookingApp.dto.WalletRechargeRequest;
import com.ride.RideBookingApp.entity.Ride;
import com.ride.RideBookingApp.entity.Transaction;
import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.exception.ActiveRideExistsException;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.UserNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private TransactionDao transactionDao;

	@Autowired
	private RideDao rideDao;

	/* 1. Recharge Wallet */
	public UserResponse rechargeWallet(WalletRechargeRequest request) {
		if (request.getAmount() < 50.0) {
			throw new InvalidInputException("Minimum wallet recharge amount is ₹50.00. Provided: ₹" + request.getAmount());
		}
		if (request.getAmount() > 50000.0) {
			throw new InvalidInputException("Maximum single wallet recharge limit is ₹50,000.00. Provided: ₹" + request.getAmount());
		}

		User user = userDao.fetchById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

		// Check email verification status
		if (!user.isEmailVerified()) {
			throw new InvalidInputException("Email is not verified for user '" + user.getEmail()
					+ "'. Please verify your email via OTP before recharging wallet.");
		}

		// Update wallet
		user.setWalletBalance(user.getWalletBalance() + request.getAmount());
		User updatedUser = userDao.saveUser(user);

		// Record Transaction
		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setAmount(request.getAmount());
		transaction.setTransactionType(request.getTransactionType() != null ? request.getTransactionType() : TransactionType.UPI);
		transaction.setDescription("Wallet recharge of ₹" + request.getAmount());
		transaction.setTransactionTime(LocalDateTime.now());
		transactionDao.saveTransaction(transaction);

		return new UserResponse("Wallet recharged successfully. Current balance: ₹" + String.format("%.2f", updatedUser.getWalletBalance()),
				updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getContactNo(),
				updatedUser.getWalletBalance(), updatedUser.isEmailVerified(), updatedUser.getAddress());
	}

	/* 2. Fetch User by ID */
	public UserResponse fetchUserById(Long id) {
		User user = userDao.fetchById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
		return new UserResponse("User fetched successfully", user.getId(), user.getName(), user.getEmail(),
				user.getContactNo(), user.getWalletBalance(), user.isEmailVerified(), user.getAddress());
	}

	/* 3. Fetch All Users */
	public List<UserResponse> fetchAllUsers() {
		List<User> users = userDao.fetchAllUsers();
		List<UserResponse> responses = new ArrayList<>();
		for (User user : users) {
			responses.add(new UserResponse("User fetched successfully", user.getId(), user.getName(),
					user.getEmail(), user.getContactNo(), user.getWalletBalance(), user.isEmailVerified(),
					user.getAddress()));
		}
		return responses;
	}

	/* 4. Update User */
	public UserResponse updateUser(Long id, RegisterUserRequest request) {
		User existingUser = userDao.fetchById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		User userToUpdate = new User();
		userToUpdate.setName(request.getName());
		userToUpdate.setEmail(request.getEmail());
		userToUpdate.setPassword(request.getPassword());
		userToUpdate.setContactNo(request.getContactNo());
		userToUpdate.setAddress(request.getAddress());

		User updatedUser = userDao.updateUser(id, userToUpdate);

		return new UserResponse("User updated successfully", updatedUser.getId(), updatedUser.getName(),
				updatedUser.getEmail(), updatedUser.getContactNo(), updatedUser.getWalletBalance(),
				updatedUser.isEmailVerified(), updatedUser.getAddress());
	}

	/* 5. Delete User */
	public String deleteUser(Long id) {
		User user = userDao.fetchById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Scenario check: check if user has active rides
		List<Ride> activeUserRides = rideDao.findActiveRidesByUserId(id);
		if (!activeUserRides.isEmpty()) {
			throw new ActiveRideExistsException("User '" + user.getName() + "' cannot be deleted because they have an active ride (#"
					+ activeUserRides.get(0).getId() + " - " + activeUserRides.get(0).getRideStatus() + ") in progress.");
		}

		userDao.deleteUser(id);
		return "User deleted successfully with id: " + id;
	}

}
