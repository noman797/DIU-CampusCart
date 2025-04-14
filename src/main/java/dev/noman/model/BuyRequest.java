package dev.noman.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerEmail;

    @ManyToOne
    private Product product;

    private boolean accepted;

    // Optional: Add a reference to the buyer if more details are needed
    @ManyToOne
    private User buyer; // Assuming User entity represents the buyer
}
