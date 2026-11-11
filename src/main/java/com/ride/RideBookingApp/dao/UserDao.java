package com.ride.RideBookingApp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.Role;
import com.ride.RideBookingApp.repository.UserRepository;

@Repository
public class UserDao {

	@Autowired
	private UserRepository userRepository;

	/* 1. save user --> save(User user) --> from UserRepository */
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	/* 2. save all users --> saveAll(List<User> users) */
	public List<User> saveAllUsers(List<User> users) {
		return userRepository.saveAll(users);
	}

	/* 3. fetch user by id --> findById(Long id) */
	public Optional<User> fetchById(Long id) {
		return userRepository.findById(id);
	}

	/* 4. find by email --> findByEmail(String email) */
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/* 5. exists by email --> existsByEmail(String email) */
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	/* 6. fetch all users --> findAll() */
	public List<User> fetchAllUsers() {
		return userRepository.findAll();
	}

	/* 7. update user --> save(T Entity) */
	public User updateUser(Long id, User newUser) {
		Optional<User> byId = userRepository.findById(id);
		if (byId.isPresent()) {
			User existingUser = byId.get();
			User finalUser = new User();

			finalUser.setId(existingUser.getId());

			// Name check: if null, empty, or Swagger default "string", keep existing
			if (newUser.getName() != null && !newUser.getName().trim().isEmpty()
					&& !newUser.getName().equalsIgnoreCase("string")) {
				finalUser.setName(newUser.getName());
			} else {
				finalUser.setName(existingUser.getName());
			}

			// Email check: if null, empty, or Swagger default "string", keep existing
			if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty()
					&& !newUser.getEmail().equalsIgnoreCase("string")) {
				finalUser.setEmail(newUser.getEmail());
			} else {
				finalUser.setEmail(existingUser.getEmail());
			}

			// Password check
			if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()
					&& !newUser.getPassword().equalsIgnoreCase("string")) {
				finalUser.setPassword(newUser.getPassword());
			} else {
				finalUser.setPassword(existingUser.getPassword());
			}

			// Contact number check
			if (newUser.getContactNo() != null && !newUser.getContactNo().trim().isEmpty()
					&& !newUser.getContactNo().equalsIgnoreCase("string")) {
				finalUser.setContactNo(newUser.getContactNo());
			} else {
				finalUser.setContactNo(existingUser.getContactNo());
			}

			// Address check
			if (newUser.getAddress() != null && !newUser.getAddress().trim().isEmpty()
					&& !newUser.getAddress().equalsIgnoreCase("string")) {
				finalUser.setAddress(newUser.getAddress());
			} else {
				finalUser.setAddress(existingUser.getAddress());
			}

			// Wallet balance check (keep existing if 0 or positive update)
			if (newUser.getWalletBalance() > 0) {
				finalUser.setWalletBalance(newUser.getWalletBalance());
			} else {
				finalUser.setWalletBalance(existingUser.getWalletBalance());
			}

			// Role
			if (newUser.getRole() != null) {
				finalUser.setRole(newUser.getRole());
			} else {
				finalUser.setRole(existingUser.getRole());
			}

			finalUser.setOtp(existingUser.getOtp());
			finalUser.setEmailVerified(existingUser.isEmailVerified());
			finalUser.setOtpExpirationTime(existingUser.getOtpExpirationTime());
			finalUser.setRides(existingUser.getRides());
			finalUser.setTransactions(existingUser.getTransactions());

			return userRepository.save(finalUser);
		}
		return null;
	}

	/* 8. delete user by id */
	public Optional<User> deleteUser(Long id) {
		Optional<User> byId = userRepository.findById(id);
		if (byId.isPresent()) {
			userRepository.deleteById(id);
			return byId;
		}
		return Optional.empty();
	}

	/* 9. count total users */
	public long countUsers() {
		return userRepository.count();
	}

	/* 10. count users by role */
	public long countByRole(Role role) {
		return userRepository.countByRole(role);
	}

}

