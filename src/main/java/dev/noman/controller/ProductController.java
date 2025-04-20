package dev.noman.controller;

import dev.noman.model.BuyRequest;
import dev.noman.model.Product;
import dev.noman.service.BuyRequestService;
import dev.noman.service.EmailService;
import dev.noman.service.ProductService;
import dev.noman.config.AppConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class ProductController {

    private static final Logger logger = Logger.getLogger(ProductController.class.getName());
    private final ProductService productService;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private EmailService mailService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private BuyRequestService buyRequestService;
    // Helper method to check if user is logged in
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userName") != null;
    }
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/sell-product")

    public String showProductForm(HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/"; // Redirect if not logged in
        }
        model.addAttribute("product", new Product());
        return "sell-product";
    }

    @PostMapping("/sell-product")
    public String submitProduct(@Valid @ModelAttribute Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile file,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null || !userEmail.endsWith("@diu.edu.bd")) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/sell-product";
        }

        product.setOwnerEmail(userEmail);

        if (bindingResult.hasErrors()) {
            logger.warning("Validation failed: " + bindingResult.getAllErrors());
            return "sell-product";
        }

        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.matches(".*\\.(jpg|png|jpeg)$")) {
                redirectAttributes.addFlashAttribute("error", "Only JPG, JPEG, and PNG files are allowed.");
                return "redirect:/sell-product";
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uniqueFileName = timestamp + "_" + fileName;

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
                product.setPhotoUrl("/uploads/" + uniqueFileName);
            } catch (IOException e) {
                logger.severe("Failed to save image: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Image upload failed!");
                return "redirect:/sell-product";
            }
        } else {
            product.setPhotoUrl("/uploads/default.jpg");
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        logger.info("Product saved successfully: " + product);
        return "redirect:/sell-product";
    }

    @GetMapping("/products/buy-products")
    public String showBuyProductsPage(@RequestParam(value = "category", required = false) String category, HttpSession session, Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/"; // Redirect if not logged in
        }
        if (category != null && !category.isEmpty()) {
            model.addAttribute("products", productService.getProductsByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("selectedCategory", "All");
        }

        model.addAttribute("categories", productService.getAllCategories());
        return "buy-products";
    }

    @PutMapping("/api/products/{id}/sold")
    public ResponseEntity<?> markProductAsSold(@PathVariable Long id, HttpSession session) {

        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) return ResponseEntity.notFound().build();

        Product product = productOpt.get();
        String userEmail = (String) session.getAttribute("userEmail");

        if (!product.getOwnerEmail().equals(userEmail)) {
            return ResponseEntity.status(403).body("Unauthorized.");
        }

        product.setSoldOut(true);
        product.setStatus(Product.ProductStatus.SOLD);
        productService.saveProduct(product);

        Optional<BuyRequest> acceptedRequestOpt = buyRequestService.getAcceptedRequest(product);
        if (acceptedRequestOpt.isPresent()) {
            mailService.sendSoldOutInvoiceToAcceptedBuyerAndSeller(product);
        }

        return ResponseEntity.ok("Marked as sold.");
    }


    @GetMapping("/product/{id}")
    public String viewProductDetails(@PathVariable Long id, HttpSession session,  Model model) {

        if (!isAuthenticated(session)) {
            return "redirect:/"; // Redirect if not logged in
        }
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-details";
        } else {
            return "redirect:/buy-products";
        }
    }

    @PostMapping("/buy-request/{id}")
    public String submitBuyRequest(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        Product product = productService.findById(id);

        if (product != null && !product.getSoldOut()) {
            String buyerEmail = (String) session.getAttribute("userEmail");

            if (buyerEmail == null || !buyerEmail.endsWith("@diu.edu.bd")) {
                redirectAttributes.addFlashAttribute("error", "You must be logged in to request.");
                return "redirect:/product/" + id;
            }

            BuyRequest request = new BuyRequest();
            request.setBuyerEmail(buyerEmail);
            request.setProduct(product);
            request.setAccepted(false);
            buyRequestService.save(request);

            String acceptLink = appConfig.getAppUrl() + "/buy-request/accept/" + request.getId();
            mailService.sendBuyRequestWithAcceptLinkToSeller(product.getOwnerEmail(), product.getName(), buyerEmail, acceptLink);

            redirectAttributes.addFlashAttribute("success", "Buy request sent to the owner.");
            return "redirect:/product/" + id;
        }

        redirectAttributes.addFlashAttribute("error", "Invalid product or already sold.");
        return "redirect:/product/" + id;
    }
    @GetMapping("/buy-request/accept/{requestId}")
    @ResponseBody
    public String acceptBuyRequest(@PathVariable Long requestId, HttpSession session) {
        List<BuyRequest> allRequests = buyRequestService.getAllRequests();
        Optional<BuyRequest> requestOpt = allRequests.stream().filter(r -> r.getId().equals(requestId)).findFirst();

        if (requestOpt.isEmpty()) {
            return "❌ Request not found.";
        }

        BuyRequest request = requestOpt.get();

        // Check if the request is already accepted
        if (request.isAccepted()) {
            return "❌ This request has already been accepted.";
        }

        // Deny all previous requests for the same product
        List<BuyRequest> productRequests = buyRequestService.getRequestsByProduct(request.getProduct());
        for (BuyRequest r : productRequests) {
            r.setAccepted(false); // Deny previous requests
        }

        // Accept the current request
        request.setAccepted(true);
        buyRequestService.saveAll(productRequests);

        // Send a notification email to the buyer
        mailService.sendBuyRequestToBuyer(
                request.getBuyerEmail(),
                request.getProduct().getName(),
                request.getProduct().getPhone()
        );

        return "✅ Buy request accepted. Buyer has been notified.";
    }



    public String generateVerificationUrl(String token) {
        return appConfig.getAppUrl() + "/api/verify?token=" + token;
    }
}