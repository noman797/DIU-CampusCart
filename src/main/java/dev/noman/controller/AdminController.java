package dev.noman.controller;

import dev.noman.model.Product;
import dev.noman.model.User;
import dev.noman.repository.ProductRepository;
import dev.noman.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final Map<String, String> admins = Map.of(
            "noman@diu.edu.bd", "noman1234",
            "supan@diu.edu.bd", "supan1234"
    );

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login"; // Return the login page view
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (admins.containsKey(email) && admins.get(email).equals(password)) {
            session.setAttribute("adminEmail", email); // Store admin email in session
            return ResponseEntity.ok(Map.of("message", "Login successful"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password"));
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "admin-dashboard"; // Return the dashboard page view
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll(); // Fetch all users from the repository
        model.addAttribute("users", users); // Add users to the model
        return "manage-users"; // Return the manage users page view
    }

    @GetMapping("/manage-products")
    public String manageProducts(Model model) {
        List<Product> products = productRepository.findAll(); // Fetch all products from the repository
        model.addAttribute("products", products); // Add products to the model
        return "manage-products"; // Return the manage products page view
    }

    @PostMapping("/deleteUser")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@RequestParam("id") Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }
    }

    @PostMapping("/deleteProduct")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@RequestParam("id") Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found."));
        }
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/admin/login"; // Redirect to the login page
    }
}