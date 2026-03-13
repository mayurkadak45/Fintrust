package com.fintrust.customer.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

	/**
	 * Send "Application received" email after customer submits new account application.
	 */
	void sendApplicationReceived(String toEmail, String applicantName);

	/**
	 * Send "Account approved" email with login credentials after admin approves the application.
	 */
	void sendApplicationApproved(String toEmail, String customerName, String username, String temporaryPassword,
			String accountNo);

	void sendDebitCardRequestReceived(String toEmail, String customerName, String accountNo);

	void sendDebitCardApproved(String toEmail, String customerName, String accountNo, String cardNumber,
			String expiryDate, String pin);

	void sendAtmPinChanged(String toEmail, String customerName, String accountNo, String cardMasked);

	void sendAtmTransactionAlert(String toEmail, String customerName, String accountNo, String type, String amount,
			String balanceAfter, String txnId, String timestamp);

}