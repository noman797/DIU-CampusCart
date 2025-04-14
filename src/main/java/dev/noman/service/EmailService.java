package dev.noman.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dev.noman.model.BuyRequest;
import dev.noman.model.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BuyRequestService buyRequestService;

    @Value("${app.url}")
    private String appUrl;

    // ✅ Send Verification Email
    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your DIU Campus Cart account";
        String verificationUrl = appUrl + "/api/verify?token=" + token;
        String message = "Click the following link to verify your email:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    // ✅ Send Buy Request Email to Seller
    public void sendBuyRequestToSeller(String toEmail, String productName) {
        String subject = "Buy Request for " + productName;
        String message = "You have received a buy request for your product: " + productName + ".\n\nClick here to accept the request.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("Failed to send Buy Request email to seller: " + e.getMessage());
        }
    }

    // ✅ Send Buy Request Accepted Email to Buyer
    public void sendBuyRequestToBuyer(String toEmail, String productName) {
        String subject = "Your Buy Request for " + productName + " has been accepted!";
        String message = "The seller has accepted your buy request for: " + productName + ". Please proceed with the transaction.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("Failed to send Buy Request accepted email to buyer: " + e.getMessage());
        }
    }

    // ✅ Send Buy Request With Accept Link to Seller
    public void sendBuyRequestWithAcceptLinkToSeller(String toEmail, String productName, String buyerEmail, String acceptLink) {
        String subject = "Buy Request for " + productName;
        String message = "Hello,\n\nYou have received a buy request for your product: " + productName +
                "\nFrom: " + buyerEmail +
                "\n\nTo accept this request, click the link below:\n" + acceptLink +
                "\n\nThank you,\nDIU CampusCart";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        try {
            mailSender.send(email);
        } catch (Exception e) {
            System.err.println("Failed to send Buy Request email to seller: " + e.getMessage());
        }
    }

    // ✅ Send Invoice Email with PDF Attachment
    public void sendInvoiceEmailWithAttachment(String toEmail, String subject, String message, byte[] pdfData, String fileName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message);

            ByteArrayResource pdfAttachment = new ByteArrayResource(pdfData);
            helper.addAttachment(fileName, pdfAttachment);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Failed to send invoice email: " + e.getMessage());
        }
    }

    // ✅ Send Sold Out Invoice to Accepted Buyer and Seller (FIXED)
    public void sendSoldOutInvoiceToAcceptedBuyerAndSeller(Product product) {
        Optional<BuyRequest> acceptedRequestOpt = buyRequestService.getAcceptedRequest(product);

        if (acceptedRequestOpt.isPresent()) {
            BuyRequest acceptedRequest = acceptedRequestOpt.get();
            String buyerEmail = acceptedRequest.getBuyerEmail();
            String sellerEmail = product.getOwnerEmail();

            byte[] invoicePdf = generateInvoicePdf(product, acceptedRequest);

            sendInvoiceEmailWithAttachment(buyerEmail,
                    "Your purchase invoice for " + product.getName(),
                    "Thank you for your purchase. Find your invoice attached.",
                    invoicePdf,
                    "invoice_buyer_" + product.getId() + ".pdf");

            sendInvoiceEmailWithAttachment(sellerEmail,
                    "Sale completed for " + product.getName(),
                    "You have successfully sold your product. Find your invoice attached.",
                    invoicePdf,
                    "invoice_seller_" + product.getId() + ".pdf");
        } else {
            System.err.println("No accepted buy request found for this product.");
        }
    }

    // ✅ Generate Invoice PDF as byte[]
    public byte[] generateInvoicePdf(Product product, BuyRequest request) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("📄 Invoice"));
            document.add(new Paragraph("Product: " + product.getName()));
            document.add(new Paragraph("Price: " + product.getPrice()));
            document.add(new Paragraph("Seller: " + product.getOwnerEmail()));
            document.add(new Paragraph("Buyer: " + request.getBuyerEmail()));
            document.add(new Paragraph("Status: Sold"));
            document.add(new Paragraph("Thank you for using DIU CampusCart!"));

            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
