package dev.noman.controller;

import dev.noman.model.User;
import dev.noman.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Check if the email is from DIU domain
        if (!user.getEmail().endsWith("@diu.edu.bd")) {
            response.put("message", "Only DIU email addresses are allowed!");
            return ResponseEntity.badRequest().body(response);
        }

        // Check password length
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            response.put("message", "Password must be at least 6 characters!");
            return ResponseEntity.badRequest().body(response);
        }

        // Register the user with a pending status and send the verification email
        try {
            String result = userService.registerUser(user); // Registration logic in the service

            response.put("message", result);

            if (result.equals("Registration successful!")) {
                // User is saved with pending status, and the verification email is sent
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("message", "An error occurred during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== LOGIN (POST) ==========
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User loginRequest, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            response.put("message", "Invalid email or password!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();

        // ✅ Check if user is verified
        if (!user.isVerified()) {
            response.put("message", "Please verify your email before logging in.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean passwordMatch = userService.checkPassword(loginRequest.getPassword(), user.getPassword());

        if (!passwordMatch) {
            response.put("message", "Invalid email or password!");
            return ResponseEntity.badRequest().body(response);
        }

        // ✅ Save user email and name in session after successful login
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getName());

        response.put("message", "Login successful!");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(HttpSession session) {
        session.invalidate();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully.");
        return ResponseEntity.ok(response);
    }
}
