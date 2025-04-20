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
            "noman@diu.edu.bd", "Noman@797",
            "supan@diu.edu.bd", "Roy@797"
    );

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ✅ Updated helper method: only admin email check
    private boolean isAdminAuthenticated(HttpSession session) {
        String email = (String) session.getAttribute("adminEmail");
        return email != null && admins.containsKey(email);
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (admins.containsKey(email) && admins.get(email).equals(password)) {
            session.setAttribute("adminEmail", email);
            return ResponseEntity.ok(Map.of("message", "Login successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password"));
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        return "admin-dashboard";
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "manage-users";
    }

    @GetMapping("/manage-products")
    public String manageProducts(Model model, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin/login";
        }
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "manage-products";
    }

    @PostMapping("/deleteUser")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@RequestParam("id") Long id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

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
    public ResponseEntity<?> deleteProduct(@RequestParam("id") Long id, HttpSession session) {
        if (!isAdminAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

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
        session.invalidate();
        return "redirect:/admin/login";
    }
}
