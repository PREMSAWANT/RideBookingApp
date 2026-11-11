package com.ride.RideBookingApp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dto.RegisterUserRequest;
import com.ride.RideBookingApp.dto.RegisterUserResponse;
import com.ride.RideBookingApp.dto.ResendOtpRequest;
import com.ride.RideBookingApp.dto.ResendOtpResponse;
import com.ride.RideBookingApp.dto.UserResponse;
import com.ride.RideBookingApp.dto.VerifyOtpRequest;
import com.ride.RideBookingApp.dto.VerifyOtpResponse;
import com.ride.RideBookingApp.emailConfig.EmailService;
import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.Role;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.InvalidOtpException;
import com.ride.RideBookingApp.exception.OtpExpiredException;
import com.ride.RideBookingApp.exception.UserAlreadyExistsException;
import com.ride.RideBookingApp.exception.UserNotFoundException;

@Service
public class AuthService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private EmailService emailService;

	/* 1. Register User */
	public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {

		if (registerUserRequest.getEmail() == null || registerUserRequest.getEmail().trim().isEmpty()
				|| registerUserRequest.getEmail().equalsIgnoreCase("string")) {
			throw new InvalidInputException("Email cannot be empty or default string.");
		}

		if (registerUserRequest.getPassword() == null || registerUserRequest.getPassword().trim().length() < 6) {
			throw new InvalidInputException("Password must be at least 6 characters long.");
		}

		if (registerUserRequest.getContactNo() != null && !registerUserRequest.getContactNo().trim().isEmpty()
				&& !registerUserRequest.getContactNo().equalsIgnoreCase("string")) {
			if (!registerUserRequest.getContactNo().trim().matches("^[6-9]\\d{9}$")) {
				throw new InvalidInputException("Invalid contact number '" + registerUserRequest.getContactNo()
						+ "'. Must be a valid 10-digit mobile number starting with 6, 7, 8, or 9.");
			}
		}

		/* 1. Check if user already exists in DB */
		if (userDao.existsByEmail(registerUserRequest.getEmail())) {
			throw new UserAlreadyExistsException("User already exists with email: " + registerUserRequest.getEmail());
		}

		/* 2. Generate a 6-digit OTP */
		String otp = emailService.generateOtp();

		/* 3. Create user entity and populate details */
		User user = new User();
		user.setName(registerUserRequest.getName());
		user.setEmail(registerUserRequest.getEmail());
		user.setPassword(registerUserRequest.getPassword());
		user.setContactNo(registerUserRequest.getContactNo());
		user.setAddress(registerUserRequest.getAddress());
		user.setWalletBalance(0.0);

		/* 4. Set OTP */
		user.setOtp(otp);

		/* 5. Set default role */
		user.setRole(Role.USER);

		/* 6. Set OTP expiration time (5 minutes from now) */
		user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));

		/* 7. Set email verification status to false */
		user.setEmailVerified(false);

		/* 8. Save user to DB */
		userDao.saveUser(user);

		/* 9. Send verification email with OTP */
		emailService.sendEmail(user.getEmail(), otp);

		/* 10. Return response */
		return new RegisterUserResponse("User registered successfully. An OTP has been sent to your email.",
				user.getEmail(), false);
	}

	/* 2. Verify Email */
	public VerifyOtpResponse verifyEmail(VerifyOtpRequest verifyOtpRequest) {

		/* 1. Check if the user exists in DB */
		User user = userDao.findByEmail(verifyOtpRequest.getEmail())
				.orElseThrow(() -> new UserNotFoundException("No user found with email: " + verifyOtpRequest.getEmail()));

		/* 2. Check if email is already verified */
		if (user.isEmailVerified()) {
			return new VerifyOtpResponse("Email is already verified.", user.getEmail(), true);
		}

		/* 3. Check if OTP exists in DB */
		if (user.getOtp() == null) {
			throw new InvalidOtpException("Invalid OTP: No OTP found for this user.");
		}

		/* 4. Check if OTP has expired */
		if (user.getOtpExpirationTime() == null || LocalDateTime.now().isAfter(user.getOtpExpirationTime())) {
			user.setOtp(null);
			user.setOtpExpirationTime(null);
			userDao.saveUser(user);
			throw new OtpExpiredException("OTP has expired. Please request a new one.");
		}

		/* 5. Compare the entered OTP with the OTP in DB */
		if (!user.getOtp().equals(verifyOtpRequest.getOtp().trim())) {
			throw new InvalidOtpException("Invalid OTP. Please enter the correct OTP.");
		}

		/* 6. Mark email as verified */
		user.setEmailVerified(true);

		/* 7. Clear OTP */
		user.setOtp(null);

		/* 8. Clear OTP expiration time */
		user.setOtpExpirationTime(null);

		/* 9. Save user changes to DB */
		userDao.saveUser(user);

		/* 10. Return success response */
		return new VerifyOtpResponse("Email has been verified successfully.", user.getEmail(), true);
	}

	/* 3. Resend OTP */
	public ResendOtpResponse resendOtp(ResendOtpRequest resendOtpRequest) {

		/* 1. Find User from DB */
		User user = userDao.findByEmail(resendOtpRequest.getEmail())
				.orElseThrow(() -> new UserNotFoundException("No user found with email: " + resendOtpRequest.getEmail()));

		/* 2. Check whether email is already verified */
		if (user.isEmailVerified()) {
			return new ResendOtpResponse("Email is already verified.", user.getEmail());
		}

		/* 3. Generate OTP */
		String otp = emailService.generateOtp();

		/* 4. Set the OTP */
		user.setOtp(otp);

		/* 5. Set Expiration Time (5 minutes from now) */
		user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));

		/* 6. Save Changes */
		userDao.saveUser(user);

		/* 7. Send OTP to email */
		emailService.sendEmail(user.getEmail(), otp);

		/* 8. Return response */
		return new ResendOtpResponse("A new OTP has been sent to your email successfully.", user.getEmail());
	}

	/* 4. Fetch All Users */
	public List<UserResponse> fetchAllUsers() {
		List<User> users = userDao.fetchAllUsers();
		List<UserResponse> responseList = new ArrayList<>();
		for (User user : users) {
			responseList.add(mapToUserResponse(user, "User fetched successfully"));
		}
		return responseList;
	}

	/* 5. Fetch User By Id */
	public UserResponse fetchUserById(Long id) {
		User user = userDao.fetchById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
		return mapToUserResponse(user, "User details fetched successfully");
	}

	/* Helper method to map User to clean UserResponse */
	public UserResponse mapToUserResponse(User user, String message) {
		return new UserResponse(message, user.getId(), user.getName(), user.getEmail(),
				user.getContactNo(), user.getWalletBalance(), user.isEmailVerified(), user.getAddress());
	}

}
