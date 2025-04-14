package dev.noman.repository;

import dev.noman.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find a product by name (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByNameIgnoreCase(@Param("name") String name);

    // Fetch only available (unsold) products for the buy section
    @Query("SELECT p FROM Product p WHERE (p.soldOut = false OR p.soldOut IS NULL)")
    List<Product> findAllAvailableProducts();

    // Find products by category, case-insensitive
    List<Product> findByCategoryIgnoreCase(String category);

    // Find products by category, excluding sold-out ones
    @Query("SELECT p FROM Product p WHERE p.category = :category AND (p.soldOut = false OR p.soldOut IS NULL)")
    List<Product> findByCategory(@Param("category") String category);

    // Fetch distinct categories from the Product table
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();

    // Find product by ID
    Optional<Product> getProductById(Long id);

}
