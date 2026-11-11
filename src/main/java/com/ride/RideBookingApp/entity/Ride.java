package com.ride.RideBookingApp.entity;

import java.time.LocalDateTime;

import com.ride.RideBookingApp.enums.RideStatus;

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
@Table(name = "rides")
@Data
@AllArgsConstructor
public class Ride {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ride_id")
	private Long id;

	@Column(name = "source")
	private String source;

	@Column(name = "destination")
	private String destination;

	@Enumerated(EnumType.STRING)
	@Column(name = "ride_status")
	private RideStatus rideStatus;

	@Column(name = "fare")
	private double fare;

	@Column(name = "booking_time")
	private LocalDateTime bookingTime;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	public Ride() {
	}
}

