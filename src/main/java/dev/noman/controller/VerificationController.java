package dev.noman.controller;

import dev.noman.model.User;
import dev.noman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        // Directly find the user by verification token
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired verification link!");
        }

        User user = optionalUser.get();

        // If already verified, inform the user
        if (user.isVerified()) {
            return ResponseEntity.ok("Your email is already verified.");
        }

        // Mark the user as verified (update status to "VERIFIED")
        user.setStatus("VERIFIED");  // Update status to VERIFIED
        user.setVerificationToken(null);  // Token is no longer needed
        userRepository.save(user);  // Save updated user to the database

        return ResponseEntity.ok("✅ Email verified successfully! You can now log in.");
    }
}
