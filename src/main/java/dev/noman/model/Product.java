package dev.noman.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    private double price;

    @Lob
    @Column(name = "photo_url", nullable = false)
    private String photoUrl = "/uploads/default.jpg";  // Default image path

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "SOLD_OUT")
    private Boolean soldOut = false;  // Default value is false
}