package dev.noman.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public byte[] generateInvoice(String productName, String buyerEmail, String sellerEmail, double price) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Header
            Font headerFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph header = new Paragraph("Invoice", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Product: " + productName));
            document.add(new Paragraph("Buyer: " + buyerEmail));
            document.add(new Paragraph("Seller: " + sellerEmail));
            document.add(new Paragraph("Price: $" + price));
            document.add(new Paragraph(" "));

            Paragraph thanks = new Paragraph("Thank you for using DIU CampusCart!");
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
