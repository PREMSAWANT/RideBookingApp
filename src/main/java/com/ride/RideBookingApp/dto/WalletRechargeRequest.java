package com.ride.RideBookingApp.dto;

import com.ride.RideBookingApp.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletRechargeRequest {

	private Long userId;
	private double amount;
	private TransactionType transactionType;

	public WalletRechargeRequest() {
	}
}

