package org.vinothgopi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.font.PdfFontFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GenerateJioBillWithQR {
    public static void main(String[] args) throws IOException, WriterException {
        String dest = "generated_jio_bill_with_qr.pdf";

        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Title: Jio Bill
        Paragraph title = new Paragraph("Jio Fiber Bill")
                .setFont(PdfFontFactory.createFont("Helvetica-Bold"))
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(10);
        document.add(title);

        // Customer details
        Paragraph customerDetails = new Paragraph(
                "Mr. Vinothgopi G\n" +
                        "4/1351-a 4/1351-a, Ganapathy Illam, Sivakasi Hussain Colony 626130\n" +
                        "Virudhunagar, Tamilnadu, India\n" +
                        "vinothgopi@hotmail.com | +918760603355\n" +
                        "Aadhaar Number: XXXX XXXX1978 | Registered Mobile: 914562359020\n" +
                        "Jio Number: 914562359020 | Document Number: 460003132482\n" +
                        "Billing Cycle Date: 11-AUG-2024 | Due Date: 20-AUG-2024\n" +
                        "Credit Limit: ₹895.00 | Security Deposit: ₹0.00")
                .setMarginBottom(20);
        document.add(customerDetails);

        // Account details
        Paragraph accountDetails = new Paragraph(
                "Account Number: 520393852919\n" +
                        "Tax Invoice Date: 11-AUG-2024\n" +
                        "Your Jio Fiber Bill From 11-JUL-2024 to 10-AUG-2024")
                .setMarginBottom(20);
        document.add(accountDetails);

        // Add Table for Bill Summary
        float[] columnWidths = {4, 2};
        Table billTable = new Table(UnitValue.createPercentArray(columnWidths));
        billTable.setWidth(UnitValue.createPercentValue(100));

        billTable.addCell(new Cell().add(new Paragraph("Previous Balance (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("582.92")));
        billTable.addCell(new Cell().add(new Paragraph("Payment Received (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("582.92")));
        billTable.addCell(new Cell().add(new Paragraph("Current Taxable Charges (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("494.00")));
        billTable.addCell(new Cell().add(new Paragraph("Taxes (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("88.92")));
        billTable.addCell(new Cell().add(new Paragraph("Total Current Month Charges (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("582.92")));
        billTable.addCell(new Cell().add(new Paragraph("Total Payable (₹)").setBold()));
        billTable.addCell(new Cell().add(new Paragraph("582.92")));
        document.add(billTable);

        // Add UPI QR Code for Payment
        String upiContent = "upi://pay?&ver=01&mode=16&orgid=700004&tr=S33I242504890704&tn=BILL PAYMENT FOR 11-JUL-2024 FOR S33I242504890704&pa=JIOC410629241806@HSBC&pn=RELIANCE RETAIL&mc=4900&am=4718.82&mid=TST5432&&&gst=719.82cgst:359.91|sgst:359.91&qrmedium=03&invoiceno=S33I242504890704&invoicedate=11-JUN-2024&InvoiceName=Mr. Vinothgopi G&&&gstin=33AABCI6363G1ZQ&&currency=INR";
        Image qrCodeImage = generateQRCodeImage(upiContent, 150, 150);
        document.add(new Paragraph("\nScan to Pay").setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));
        document.add(qrCodeImage.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER));

        // Add Table for Usage Summary
        Table usageTable = new Table(UnitValue.createPercentArray(columnWidths));
        usageTable.setWidth(UnitValue.createPercentValue(100));

        usageTable.addCell(new Cell().add(new Paragraph("Usage Charges").setBold()));
        usageTable.addCell(new Cell().add(new Paragraph("₹0.00")));
        usageTable.addCell(new Cell().add(new Paragraph("Monthly Plan Charges").setBold()));
        usageTable.addCell(new Cell().add(new Paragraph("₹494.00")));
        usageTable.addCell(new Cell().add(new Paragraph("Total Usage Summary").setBold()));
        usageTable.addCell(new Cell().add(new Paragraph("₹0.00")));
        document.add(usageTable);

        // Add Taxes Section
        Paragraph taxes = new Paragraph(
                "CGST (9%): ₹44.46\n" +
                        "SGST (9%): ₹44.46\n" +
                        "Total Taxes: ₹88.92")
                .setMarginTop(20)
                .setMarginBottom(20);
        document.add(taxes);

        // Payment options
        Paragraph paymentOptions = new Paragraph(
                "Payment Options:\n" +
                        "- Set JioAutoPay through MyJio App\n" +
                        "- Pay bills using credit/debit card, net banking, UPI\n" +
                        "- ACH mandate on bank account\n" +
                        "- Scan and pay with UPI QR Code")
                .setMarginBottom(20);
        document.add(paymentOptions);

        // Footer Information
        Paragraph footer = new Paragraph(
                "Reliance Jio Infocomm Ltd\n" +
                        "Registered Office: Office-101, Saffron, Nr. Centre Point, Panchwati 5 Rasta, Ambawadi, Ahmedabad-380006, Gujarat, India\n" +
                        "GSTIN: 33AABCI6363G1ZQ | CIN: U72900GJ2007PLC105869\n" +
                        "Reach Us: Call 199 (from a Jio number) or 1800-89-69999 (from other networks)\n" +
                        "Write to Jiofibercare@jio.com | Visit www.jio.com")
                .setFontSize(10)
                .setMarginTop(20);
        document.add(footer);

        // Close the document
        document.close();
        System.out.println("PDF generated successfully with UPI QR code.");
    }

    // Method to generate QR code image
    private static Image generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);  // Black for QR code, white for background
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] pngData = byteArrayOutputStream.toByteArray();
        ImageData imageData = ImageDataFactory.create(pngData);
        return new Image(imageData);
    }
}
