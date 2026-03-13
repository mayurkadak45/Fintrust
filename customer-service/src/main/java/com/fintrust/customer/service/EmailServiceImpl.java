package com.fintrust.customer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from-name:FinTrust Bank}")
    private String fromName;

    /**
     * Send "Application received" email after customer submits new account application.
     */
    @Override
	@Async
    public void sendApplicationReceived(String toEmail, String applicantName) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping application-received email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – Account Application Received";
        String body = """
            <h2>Dear %s,</h2>
            <p>Thank you for applying for an account with FinTrust Bank.</p>
            <p>We have received your application and it is under review. You will receive an email once your account has been approved by our team.</p>
            <p>If you have any questions, please contact our support team.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(applicantName);
        sendHtml(toEmail, subject, body);
    }

    /**
     * Send "Account approved" email with login credentials after admin approves the application.
     */
    @Override
	@Async
    public void sendApplicationApproved(String toEmail, String customerName, String username, String temporaryPassword, String accountNo) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping approval email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – Your Account Has Been Approved";
        String body = """
            <h2>Dear %s,</h2>
            <p>Your account application has been approved. You can now log in to FinTrust Bank.</p>
            <h3>Your login credentials</h3>
            <ul>
                <li><strong>Username:</strong> %s</li>
                <li><strong>Temporary password:</strong> %s</li>
                <li><strong>Account number:</strong> %s</li>
            </ul>
            <p>Please log in at the bank portal and change your password after first login for security.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(customerName, username, temporaryPassword, accountNo);
        sendHtml(toEmail, subject, body);
    }

    private void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
	@Async
    public void sendDebitCardRequestReceived(String toEmail, String customerName, String accountNo) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping debit-card request email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – Debit Card Request Received";
        String body = """
            <h2>Dear %s,</h2>
            <p>We have received your debit card request for account <strong>%s</strong>.</p>
            <p>Our team will review and you will receive an approval email with your card details once approved.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(customerName, accountNo);
        sendHtml(toEmail, subject, body);
    }

    @Override
	@Async
    public void sendDebitCardApproved(String toEmail, String customerName, String accountNo, String cardNumber, String expiryDate, String pin) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping debit-card approval email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – Debit Card Approved";
        String body = """
            <h2>Dear %s,</h2>
            <p>Your debit card request for account <strong>%s</strong> has been approved.</p>
            <h3>Your card details</h3>
            <ul>
                <li><strong>Card number:</strong> %s</li>
                <li><strong>Expiry date:</strong> %s</li>
                <li><strong>ATM PIN (one-time view):</strong> %s</li>
            </ul>
            <p>Please change your ATM PIN at the Virtual ATM after first use.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(customerName, accountNo, cardNumber, expiryDate, pin != null ? pin : "****");
        sendHtml(toEmail, subject, body);
    }

    @Override
	@Async
    public void sendAtmPinChanged(String toEmail, String customerName, String accountNo, String cardMasked) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping PIN-changed email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – ATM PIN Changed";
        String body = """
            <h2>Dear %s,</h2>
            <p>Your ATM PIN has been successfully changed for account <strong>%s</strong>.</p>
            <p><strong>Card:</strong> %s</p>
            <p>If you did not perform this action, please contact support immediately.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(customerName, accountNo, cardMasked != null ? cardMasked : "****");
        sendHtml(toEmail, subject, body);
    }

    @Override
	@Async
    public void sendAtmTransactionAlert(String toEmail, String customerName, String accountNo, String type, String amount, String balanceAfter, String txnId, String timestamp) {
        if (mailSender == null || !mailEnabled || fromEmail == null || fromEmail.isBlank()) {
            log.debug("Mail not configured or disabled; skipping ATM txn email to {}", toEmail);
            return;
        }
        String subject = "FinTrust Bank – ATM " + (type != null ? type : "Transaction") + " Alert";
        String body = """
            <h2>Dear %s,</h2>
            <p>An ATM transaction occurred on your account <strong>%s</strong>.</p>
            <ul>
                <li><strong>Type:</strong> %s</li>
                <li><strong>Amount:</strong> %s</li>
                <li><strong>Balance after:</strong> %s</li>
                <li><strong>Transaction ID:</strong> %s</li>
                <li><strong>Time:</strong> %s</li>
            </ul>
            <p>If this wasn’t you, please contact support immediately.</p>
            <p>Best regards,<br/><strong>FinTrust Bank</strong></p>
            """.formatted(customerName, accountNo, type, amount, balanceAfter, txnId, timestamp);
        sendHtml(toEmail, subject, body);
    }
}
