package dev.noman.controller;

import dev.noman.model.Product;
import dev.noman.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
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

    @GetMapping("/sell-product")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "sell-product";
    }

    @PostMapping("/sell-product")
    public String submitProduct(@Valid @ModelAttribute Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.warning("Validation failed: " + bindingResult.getAllErrors());
            return "sell-product";
        }

        if (!file.isEmpty()) {
            // Validate file type
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.matches(".*\\.(jpg|png|jpeg)$")) {
                redirectAttributes.addFlashAttribute("error", "Only JPG, JPEG, and PNG files are allowed.");
                return "redirect:/sell-product";
            }

            // Generate unique file name
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uniqueFileName = timestamp + "_" + fileName;

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.createDirectories(path.getParent()); // Ensure directory exists
                Files.write(path, bytes);
                product.setPhotoUrl("/uploads/" + uniqueFileName);  // Save relative URL
            } catch (IOException e) {
                logger.severe("Failed to save image: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Image upload failed!");
                return "redirect:/sell-product";
            }
        } else {
            product.setPhotoUrl("/uploads/default.jpg"); // Default image if none uploaded
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        logger.info("Product saved successfully: " + product);
        return "redirect:/sell-product";
    }

    @GetMapping("/products/buy-products")
    public String showBuyProductsPage(Model model) {
        // Fetch all products and pass them to the model
        model.addAttribute("products", productService.getAllProducts());
        return "buy-products";
    }

    @PutMapping("/api/products/{id}/sold")
    public ResponseEntity<?> markProductAsSold(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setSoldOut(true);
            productService.saveProduct(product);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}