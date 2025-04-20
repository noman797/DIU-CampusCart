package dev.noman.controller;

import dev.noman.model.User;
import dev.noman.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    // GET: Home Page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // GET: Registration Page
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registration";
    }

    // GET: Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Helper method to check if user is logged in
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userName") != null;
    }

    // GET: Dashboard Page (authenticated users only)
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/"; // Redirect if not logged in
        }

        String userName = (String) session.getAttribute("userName");
        model.addAttribute("userName", userName);  // Pass to Thymeleaf
        return "dashboard";
    }

    // POST: Logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear the session
        return "redirect:/";  // Redirect to home page
    }
}
