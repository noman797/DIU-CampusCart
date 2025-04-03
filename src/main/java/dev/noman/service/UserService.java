package dev.noman.service;

import dev.noman.model.User;
import dev.noman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Method to register a new user
    public String registerUser(User user) {
        // 1) @diu.edu.bd check
        if (!user.getEmail().endsWith("@diu.edu.bd")) {
            return "Only DIU email addresses are allowed!";
        }

        // 2) Password length check
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return "Password must be at least 6 characters!";
        }

        // 3) Check for existing email
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            return "Email already in use!";
        }

        // 4) Encrypt (hash) password before save
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        userRepository.save(user);
        return "Registration successful!";
    }

    // Method to find a user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Method to check if the password matches the stored encoded password
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Method to delete a user by ID
    public void deleteUser(Long userId) {
        // Find user by ID and delete if exists
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(value -> userRepository.delete(value));
    }

    // Method to get a user by ID
    public User getUserById(Long userId) {
        // Return the user if exists, otherwise throw exception or return null
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null); // Returns null if user is not found
    }

    // Method to get all users
    public List<User> getAllUsers() {
        // Return all users from the repository
        return userRepository.findAll();
    }
}