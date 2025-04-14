package dev.noman.service;

import dev.noman.model.BuyRequest;
import dev.noman.model.Product;
import dev.noman.repository.BuyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuyRequestService {

    @Autowired
    private BuyRequestRepository buyRequestRepository;

    // Save a single BuyRequest
    public void save(BuyRequest request) {
        buyRequestRepository.save(request);
    }

    // Save multiple BuyRequests
    public void saveAll(List<BuyRequest> requests) {
        buyRequestRepository.saveAll(requests);
    }

    // Get all BuyRequests
    public List<BuyRequest> getAllRequests() {
        return buyRequestRepository.findAll();
    }

    // Get all BuyRequests for a specific product
    public List<BuyRequest> getRequestsByProduct(Product product) {
        return buyRequestRepository.findByProduct(product);
    }

    // Get the accepted BuyRequest for a specific product
    public Optional<BuyRequest> getAcceptedRequest(Product product) {
        return buyRequestRepository.findByProductAndAccepted(product, true);
    }
}
