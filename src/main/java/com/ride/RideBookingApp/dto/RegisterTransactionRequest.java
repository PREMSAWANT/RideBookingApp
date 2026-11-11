package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterTransactionRequest {

	private Long userId;
	private TransactionType transactionType;
	private double amount;
	private String description;

	public RegisterTransactionRequest() {
	}
}

