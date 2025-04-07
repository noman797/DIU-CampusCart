package dev.noman.controller;

import dev.noman.model.User;
import dev.noman.service.UserService;
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

    // ========== REGISTER (POST) ==========
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (!user.getEmail().endsWith("@diu.edu.bd")) {
            response.put("message", "Only DIU email addresses are allowed!");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            response.put("message", "Password must be at least 6 characters!");
            return ResponseEntity.badRequest().body(response);
        }

        String result = userService.registerUser(user);
        response.put("message", result);

        if (result.equals("Registration successful!")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
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
        boolean passwordMatch = userService.checkPassword(loginRequest.getPassword(), user.getPassword());

        if (!passwordMatch) {
            response.put("message", "Invalid email or password!");
            return ResponseEntity.badRequest().body(response);
        }

        // ✅ Save user email in session after successful login
        session.setAttribute("userEmail", user.getEmail());

        response.put("message", "Login successful!");
        return ResponseEntity.ok(response);
    }

    // ========== LOGOUT (GET) ==========
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(HttpSession session) {
        session.invalidate();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully.");
        return ResponseEntity.ok(response);
    }
}
