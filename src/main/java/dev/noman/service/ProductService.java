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

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
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
}