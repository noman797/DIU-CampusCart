package dev.noman.controller;

import dev.noman.model.Product;
import dev.noman.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/products") // API route for frontend calls
@CrossOrigin(origins = "http://localhost:8080") // Allow frontend access
public class ProductApiController {

    private static final Logger logger = Logger.getLogger(ProductApiController.class.getName());
    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    // REST API to return JSON response for frontend
    @GetMapping
    public List<Product> getAllProducts() {
        logger.info("Fetching all products...");
        return productService.getAllProducts();
    }
}