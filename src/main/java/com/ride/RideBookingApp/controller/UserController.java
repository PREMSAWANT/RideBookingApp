package com.ride.RideBookingApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride.RideBookingApp.dto.RegisterUserRequest;
import com.ride.RideBookingApp.dto.UserResponse;
import com.ride.RideBookingApp.dto.WalletRechargeRequest;
import com.ride.RideBookingApp.service.UserService;

@RestController
@RequestMapping({"/api/user", "/api/users"})
public class UserController {

	@Autowired
	private UserService userService;

	/* 1. Recharge Wallet */
	@PostMapping("/rechargeWallet")
	public ResponseEntity<UserResponse> rechargeWallet(@RequestBody WalletRechargeRequest request) {
		UserResponse response = userService.rechargeWallet(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 2. Fetch User by ID */
	@GetMapping("/fetchById/{id}")
	public ResponseEntity<UserResponse> fetchById(@PathVariable Long id) {
		UserResponse response = userService.fetchUserById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 3. Fetch All Users */
	@GetMapping("/getAll")
	public ResponseEntity<List<UserResponse>> fetchAllUsers() {
		List<UserResponse> responses = userService.fetchAllUsers();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 4. Update User */
	@PutMapping("/update/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody RegisterUserRequest request) {
		UserResponse response = userService.updateUser(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 5. Delete User */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		String result = userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

}
