package com.ride.RideBookingApp.entity;

import java.time.LocalDateTime;

import com.ride.RideBookingApp.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type")
	private TransactionType transactionType;

	@Column(name = "amount")
	private double amount;

	@Column(name = "description")
	private String description;

	@Column(name = "transaction_time")
	private LocalDateTime transactionTime;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Transaction() {
	}
}

