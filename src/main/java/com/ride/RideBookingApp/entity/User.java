package com.ride.RideBookingApp.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.ride.RideBookingApp.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "user_name")
	private String name;

	@Column(name = "user_email", unique = true, nullable = false)
	private String email;

	@Column(name = "user_password")
	private String password;

	@Column(name = "user_contact_no")
	private String contactNo;

	@Column(name = "wallet_balance")
	private double walletBalance;

	@Column(name = "user_otp")
	private String otp;

	@Column(name = "is_email_verified")
	private boolean isEmailVerified;

	@Column(name = "otp_expiration_time")
	private LocalDateTime otpExpirationTime;

	@Column(name = "user_address")
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role")
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Ride> rides;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Transaction> transactions;

	public User() {
	}
}

