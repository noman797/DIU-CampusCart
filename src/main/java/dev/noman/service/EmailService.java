package dev.noman.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Inject the URL from application.properties
    @Value("${app.url}")
    private String appUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your DIU Campus Cart account";
        String verificationUrl = appUrl + "/api/verify?token=" + token;
        String message = "Click the following link to verify your email:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
        } catch (Exception e) {
            // You can log the error or handle it as needed
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
}
