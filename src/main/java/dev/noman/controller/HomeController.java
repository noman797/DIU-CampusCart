package dev.noman.controller;

import dev.noman.model.User;
import org.springframework.ui.Model;
import dev.noman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

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

    // GET: Dashboard Page
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Example user data
        User user = new User();
        user.setFullName("John Doe");
        user.setEmail("john@diu.edu.bd");

        model.addAttribute("user", user);
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout() {
        // Perform any necessary logout logic
        return "redirect:/";  // Redirect to the login page
    }


}