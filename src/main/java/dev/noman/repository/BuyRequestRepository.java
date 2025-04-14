package dev.noman.repository;

import dev.noman.model.BuyRequest;
import dev.noman.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuyRequestRepository extends JpaRepository<BuyRequest, Long> {
    List<BuyRequest> findByProduct(Product product);
    Optional<BuyRequest> findByProductAndAcceptedTrue(Product product);

    Optional<BuyRequest> findByProductAndAccepted(Product product, boolean b);
}
