package dev.noman.controller;

import dev.noman.model.Product;
import dev.noman.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class ProductController {

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());
    private final ProductService productService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Show sell form
    @GetMapping("/sell-product")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "sell-product";
    }

    // Submit a product (add owner email from session)
    @PostMapping("/sell-product")
    public String submitProduct(@Valid @ModelAttribute Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile file,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        // Set ownerEmail from logged-in user
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null || !userEmail.endsWith("@diu.edu.bd")) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/sell-product";
        }

        product.setOwnerEmail(userEmail);

        if (bindingResult.hasErrors()) {
            logger.warning("Validation failed: " + bindingResult.getAllErrors());
            return "sell-product";
        }

        // Handle image upload
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.matches(".*\\.(jpg|png|jpeg)$")) {
                redirectAttributes.addFlashAttribute("error", "Only JPG, JPEG, and PNG files are allowed.");
                return "redirect:/sell-product";
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uniqueFileName = timestamp + "_" + fileName;

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
                product.setPhotoUrl("/uploads/" + uniqueFileName);
            } catch (IOException e) {
                logger.severe("Failed to save image: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Image upload failed!");
                return "redirect:/sell-product";
            }
        } else {
            product.setPhotoUrl("/uploads/default.jpg");
        }

        // Save product to the database
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        logger.info("Product saved successfully: " + product);
        return "redirect:/sell-product";
    }

    // Buy Products page with category filter
    @GetMapping("/products/buy-products")
    public String showBuyProductsPage(@RequestParam(value = "category", required = false) String category, Model model) {
        if (category != null && !category.isEmpty()) {
            model.addAttribute("products", productService.getProductsByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("selectedCategory", "All");
        }

        model.addAttribute("categories", productService.getAllCategories());
        return "buy-products";
    }

    // Mark product as sold (only by owner)
    @PutMapping("/api/products/{id}/sold")
    public ResponseEntity<?> markProductAsSold(@PathVariable Long id, HttpSession session) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Get logged-in user email from session
            String userEmail = (String) session.getAttribute("userEmail");
            if (userEmail == null || !userEmail.equals(product.getOwnerEmail())) {
                return ResponseEntity.status(403).body("You are not the owner of this product.");
            }

            // Mark the product as sold
            product.setSoldOut(true);
            productService.saveProduct(product);
            return ResponseEntity.ok("Product marked as sold.");
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-details";
        } else {
            return "redirect:/buy-products";
        }
    }

}
