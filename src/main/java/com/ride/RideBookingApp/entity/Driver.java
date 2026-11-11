package com.ride.RideBookingApp.entity;

import java.util.List;

import com.ride.RideBookingApp.enums.DriverStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "drivers")
@Data
@AllArgsConstructor
public class Driver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "driver_id")
	private Long id;

	@Column(name = "driver_name")
	private String name;

	@Column(name = "driver_email", unique = true, nullable = false)
	private String email;

	@Column(name = "driver_contact_no")
	private String contactNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "driver_status")
	private DriverStatus driverStatus;

	@OneToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
	private List<Ride> rides;

	public Driver() {
	}
}

