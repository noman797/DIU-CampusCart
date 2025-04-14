package dev.noman.service;

import dev.noman.model.Product;
import dev.noman.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }



    public Product saveProduct(Product product) {
        if (product.getPhotoUrl() == null || product.getPhotoUrl().isEmpty()) {
            product.setPhotoUrl("/uploads/default.jpg");  // Set default image if empty
        }
        return productRepository.save(product);
    }

    // Fetch single product details
    public Optional<Product> getProductDetailsById(Long id) {
        return productRepository.findById(id);
    }




    public Optional<Product> findProductByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return productRepository.findByNameIgnoreCase(name.trim());
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }



    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public String updateProductPhoto(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.createDirectories(path.getParent()); // Ensure directory exists
                Files.write(path, bytes);
                return "/uploads/" + file.getOriginalFilename();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "/uploads/default.jpg"; // Return default if failed
    }



    public List<Product> getProductsByCategory(String category) {
        return (List<Product>) productRepository.findByCategory(category);
    }



    public List<String> getAllCategories() {
        // Assuming Product has a 'category' field and fetching distinct categories
        return productRepository.findDistinctCategories();
    }

    // New method to mark product as sold
    public String markProductAsSold(Long productId, String loggedInUserEmail) {
        // Fetch the product by its ID from the repository
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            return "Product not found!";  // If the product is not found, return a message
        }

        Product product = optionalProduct.get();

        // Check if the logged-in user's email matches the product owner's email
        if (!product.getOwnerEmail().equals(loggedInUserEmail)) {
            return "You are not the owner of this product!";  // If the user is not the owner, return a message
        }

        // Mark the product as sold
        product.setSoldOut(true);  // Set the 'soldOut' status to true
        productRepository.save(product);  // Save the updated product back to the database

        return "Product marked as sold!";  // Return success message
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Method to find a product by ID
    public Product findProductById(Long id) {
        // Fetch the product from the database
        Optional<Product> productOptional = productRepository.findById(id);

        // If product is found, return it, else return null or throw exception
        if (productOptional.isPresent()) {
            return productOptional.get();
        } else {
            // Handle the case where the product is not found, e.g., throw an exception or return null
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    // Save or update the product in the database
    public void save(Product product) {
        productRepository.save(product);  // This saves the product to the database
    }

    // Find a product by its ID
    public Product findById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.orElse(null);  // Return the product if found, else return null
    }
}