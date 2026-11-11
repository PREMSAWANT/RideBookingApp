package com.ride.RideBookingApp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Transaction;
import com.ride.RideBookingApp.enums.TransactionType;
import com.ride.RideBookingApp.repository.TransactionRepository;

@Repository
public class TransactionDao {

	@Autowired
	private TransactionRepository transactionRepository;

	/* 1. save transaction --> save(Transaction transaction) */
	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	/* 2. save all transactions --> saveAll(List<Transaction> transactions) */
	public List<Transaction> saveAllTransactions(List<Transaction> transactions) {
		return transactionRepository.saveAll(transactions);
	}

	/* 3. fetch transaction by id --> findById(Long id) */
	public Optional<Transaction> fetchById(Long id) {
		return transactionRepository.findById(id);
	}

	/* 4. fetch all transactions --> findAll() */
	public List<Transaction> fetchAllTransactions() {
		return transactionRepository.findAll();
	}

	/* 5. fetch transactions by user id */
	public List<Transaction> findByUserId(Long userId) {
		return transactionRepository.findByUserId(userId);
	}

	/* 6. fetch transactions by transaction type */
	public List<Transaction> findByTransactionType(TransactionType transactionType) {
		return transactionRepository.findByTransactionType(transactionType);
	}

	/* 7. delete transaction by id */
	public Optional<Transaction> deleteTransaction(Long id) {
		Optional<Transaction> byId = transactionRepository.findById(id);
		if (byId.isPresent()) {
			transactionRepository.deleteById(id);
			return byId;
		}
		return Optional.empty();
	}

	/* 8. count total transactions */
	public long countTransactions() {
		return transactionRepository.count();
	}

}

