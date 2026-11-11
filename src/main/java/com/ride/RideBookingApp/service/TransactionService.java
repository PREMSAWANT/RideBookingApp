package com.ride.RideBookingApp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.RideBookingApp.dao.TransactionDao;
import com.ride.RideBookingApp.dao.UserDao;
import com.ride.RideBookingApp.dto.RegisterTransactionRequest;
import com.ride.RideBookingApp.dto.TransactionResponse;
import com.ride.RideBookingApp.entity.Transaction;
import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.exception.InvalidInputException;
import com.ride.RideBookingApp.exception.TransactionNotFoundException;
import com.ride.RideBookingApp.exception.UserNotFoundException;

@Service
public class TransactionService {

	@Autowired
	private TransactionDao transactionDao;

	@Autowired
	private UserDao userDao;

	/* 1. Create Transaction */
	public TransactionResponse createTransaction(RegisterTransactionRequest request) {
		if (request.getAmount() <= 0) {
			throw new InvalidInputException("Transaction amount must be greater than 0.");
		}

		User user = userDao.fetchById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setAmount(request.getAmount());
		transaction.setTransactionType(request.getTransactionType() != null ? request.getTransactionType() : TransactionType.UPI);
		transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Transaction of ₹" + request.getAmount());
		transaction.setTransactionTime(LocalDateTime.now());

		Transaction savedTransaction = transactionDao.saveTransaction(transaction);

		return mapToTransactionResponse(savedTransaction, "Transaction recorded successfully");
	}

	/* 2. Fetch Transaction by ID */
	public TransactionResponse fetchTransactionById(Long id) {
		Transaction transaction = transactionDao.fetchById(id)
				.orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
		return mapToTransactionResponse(transaction, "Transaction fetched successfully");
	}

	/* 3. Fetch All Transactions */
	public List<TransactionResponse> fetchAllTransactions() {
		List<Transaction> transactions = transactionDao.fetchAllTransactions();
		List<TransactionResponse> responses = new ArrayList<>();
		for (Transaction transaction : transactions) {
			responses.add(mapToTransactionResponse(transaction, "Transaction fetched successfully"));
		}
		return responses;
	}

	/* 4. Fetch Transactions by User ID */
	public List<TransactionResponse> fetchTransactionsByUserId(Long userId) {
		userDao.fetchById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
		List<Transaction> transactions = transactionDao.findByUserId(userId);
		List<TransactionResponse> responses = new ArrayList<>();
		for (Transaction transaction : transactions) {
			responses.add(mapToTransactionResponse(transaction, "Transaction fetched successfully"));
		}
		return responses;
	}

	/* 5. Fetch Transactions by Type */
	public List<TransactionResponse> fetchTransactionsByType(TransactionType type) {
		List<Transaction> transactions = transactionDao.findByTransactionType(type);
		List<TransactionResponse> responses = new ArrayList<>();
		for (Transaction transaction : transactions) {
			responses.add(mapToTransactionResponse(transaction, "Transaction fetched successfully"));
		}
		return responses;
	}

	/* Helper method to map Transaction to clean TransactionResponse */
	public TransactionResponse mapToTransactionResponse(Transaction transaction, String message) {
		String userName = transaction.getUser() != null ? transaction.getUser().getName() : null;

		return new TransactionResponse(message, transaction.getId(), userName,
				transaction.getTransactionType(), transaction.getAmount(), transaction.getDescription(),
				transaction.getTransactionTime());
	}

}
