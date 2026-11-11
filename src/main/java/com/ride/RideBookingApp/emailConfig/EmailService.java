package com.ride.RideBookingApp.emailConfig;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired(required = false)
	private JavaMailSender javaMailSender;

	/* 1. send email --> send(SimpleMailMessage message) --> void --> JavaMailSender<I> --> JavaMailSender Dependency */
	public void sendEmail(String email, String otp) {
		try {
			if (javaMailSender != null) {
				SimpleMailMessage mailMessage = new SimpleMailMessage();
				mailMessage.setFrom("premsawant600@gmail.com");
				/* 1. set reciever's email --> setTo(String s) */
				mailMessage.setTo(email);

				/* 2. set Subject --> setSubject(String text) */
				mailMessage.setSubject("RideBookingApp - Email Verification OTP");

				/* 3. set OTP --> setText(String text) --> OTP */
				mailMessage.setText("Your OTP is : " + otp);

				javaMailSender.send(mailMessage);
			} else {
				System.out.println("JavaMailSender bean offline. Generated OTP for " + email + " is: " + otp);
			}
		} catch (Exception ex) {
			System.out.println("Could not dispatch SMTP email (offline network): " + ex.getMessage() + ". OTP: " + otp);
		}
	}

	/* 2. send ride receipt */
	public void sendRideReceipt(String email, String receiptContent) {
		try {
			if (javaMailSender != null) {
				SimpleMailMessage mailMessage = new SimpleMailMessage();
				mailMessage.setFrom("premsawant600@gmail.com");
				mailMessage.setTo(email);
				mailMessage.setSubject("RideBookingApp - Ride Receipt");
				mailMessage.setText(receiptContent);
				javaMailSender.send(mailMessage);
			} else {
				System.out.println("JavaMailSender bean offline. Receipt for " + email + ":\n" + receiptContent);
			}
		} catch (Exception ex) {
			System.out.println("Could not dispatch ride receipt: " + ex.getMessage());
		}
	}

	/* 3. generate OTP --> Random<C> --> java.util package */
	public String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

}

