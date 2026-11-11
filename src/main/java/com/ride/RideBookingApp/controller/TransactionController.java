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

import com.ride.RideBookingApp.dto.RegisterTransactionRequest;
import com.ride.RideBookingApp.dto.TransactionResponse;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.service.TransactionService;

@RestController
@RequestMapping({"/api/transaction", "/api/transactions"})
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	/* 1. Record Transaction */
	@PostMapping("/create")
	public ResponseEntity<TransactionResponse> createTransaction(@RequestBody RegisterTransactionRequest request) {
		TransactionResponse response = transactionService.createTransaction(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* 2. Fetch Transaction by ID */
	@GetMapping("/fetchById/{id}")
	public ResponseEntity<TransactionResponse> fetchById(@PathVariable Long id) {
		TransactionResponse response = transactionService.fetchTransactionById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/* 3. Fetch All Transactions */
	@GetMapping("/getAll")
	public ResponseEntity<List<TransactionResponse>> fetchAllTransactions() {
		List<TransactionResponse> responses = transactionService.fetchAllTransactions();
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 4. Fetch Transactions by User ID */
	@GetMapping("/byUser/{userId}")
	public ResponseEntity<List<TransactionResponse>> fetchByUserId(@PathVariable Long userId) {
		List<TransactionResponse> responses = transactionService.fetchTransactionsByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	/* 5. Fetch Transactions by Type */
	@GetMapping("/byType/{type}")
	public ResponseEntity<List<TransactionResponse>> fetchByType(@PathVariable TransactionType type) {
		List<TransactionResponse> responses = transactionService.fetchTransactionsByType(type);
		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

}

