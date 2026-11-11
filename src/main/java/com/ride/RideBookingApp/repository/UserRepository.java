package com.ride.RideBookingApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ride.RideBookingApp.entity.User;
import com.ride.RideBookingApp.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/* fetch user by email */
	Optional<User> findByEmail(String email);

	/* exists by email */
	boolean existsByEmail(String email);

	/* fetch users by role */
	List<User> findByRole(Role role);

	/* count users by role */
	long countByRole(Role role);

}

