package com.ride.RideBookingApp.dto;

import java.time.LocalDateTime;

import com.ride.RideBookingApp.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {

	private String message;
	private Long id;
	private String userName;
	private TransactionType transactionType;
	private double amount;
	private String description;
	private LocalDateTime transactionTime;

	public TransactionResponse() {
	}
}
