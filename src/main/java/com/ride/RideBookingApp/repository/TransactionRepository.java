package com.ride.RideBookingApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.Transaction;
import com.ride.RideBookingApp.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	/* fetch transactions by user id */
	List<Transaction> findByUserId(Long userId);

	/* fetch transactions by type */
	List<Transaction> findByTransactionType(TransactionType transactionType);

}

