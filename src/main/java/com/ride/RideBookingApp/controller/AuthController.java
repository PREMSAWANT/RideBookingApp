package com.ride.RideBookingApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride.RideBookingApp.dto.RegisterUserRequest;
import com.ride.RideBookingApp.dto.RegisterUserResponse;
import com.ride.RideBookingApp.dto.ResendOtpRequest;
import com.ride.RideBookingApp.dto.ResendOtpResponse;
import com.ride.RideBookingApp.dto.UserResponse;
import com.ride.RideBookingApp.dto.VerifyOtpRequest;
import com.ride.RideBookingApp.dto.VerifyOtpResponse;
import com.ride.RideBookingApp.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	/* 1. Register User */
	@PostMapping("/register")
	public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserRequest request) {
		RegisterUserResponse response = authService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* 2. Verify Email OTP */
	@PostMapping("/verifyEmail")
	public ResponseEntity<VerifyOtpResponse> verifyEmail(@RequestBody VerifyOtpRequest request) {
		VerifyOtpResponse response = authService.verifyEmail(request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	/* 3. Resend OTP */
	@PostMapping("/resendOtp")
	public ResponseEntity<ResendOtpResponse> resendOtp(@RequestBody ResendOtpRequest request) {
		ResendOtpResponse response = authService.resendOtp(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 4. Fetch All Registered Users */
	@GetMapping("/getAll")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> responses = authService.fetchAllUsers();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 5. Fetch User by ID */
	@GetMapping("/getById/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		UserResponse response = authService.fetchUserById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
