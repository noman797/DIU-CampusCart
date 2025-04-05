package dev.noman.repository;

import dev.noman.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find a product by name (case-insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByNameIgnoreCase(@Param("name") String name);

//    // Fetch available products by category with pagination (excluding sold-out products)
//    @Query("SELECT p FROM Product p WHERE p.category = :category AND (p.soldOut = false OR p.soldOut IS NULL)")
//    Page<Product> findByCategory(@Param("category") String category);

    // Fetch only available (unsold) products for the buy section
    @Query("SELECT p FROM Product p WHERE (p.soldOut = false OR p.soldOut IS NULL)")
    List<Product> findAllAvailableProducts();

//    // 🔥 Get all distinct categories
//    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
//    List<String> findDistinctCategories();

    List<Product> findByCategoryIgnoreCase(String category);
    @Query("SELECT p FROM Product p WHERE p.category = :category AND (p.soldOut = false OR p.soldOut IS NULL)")
    List<Product> findByCategory(@Param("category") String category);
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();


}
