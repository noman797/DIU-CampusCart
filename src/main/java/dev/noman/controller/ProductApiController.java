package dev.noman.controller;

import dev.noman.model.Product;
import dev.noman.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductApiController {

    private static final Logger logger = Logger.getLogger(ProductApiController.class.getName());
    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }



    // Fetch products by category, or all products if no category is provided
    @GetMapping
    public List<Product> getProductsByCategory(@RequestParam(value = "category", required = false) String category) {
        logger.info("Fetching products for category: " + category);

        if (category != null && !category.isEmpty()) {
            return productService.getProductsByCategory(category); // Fetch products by category
        } else {
            return productService.getAllProducts(); // Fetch all products if no category is provided
        }
    }
}
