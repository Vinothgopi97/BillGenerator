package org.vinothgopi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BillGenerator {
    public static void main(String[] args) throws WriterException {
        String dest = "PDFWithImage.pdf"; // Output file

        // Create a PdfWriter object
        try (PdfWriter writer = new PdfWriter(dest);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            // Load the image
            String imagePath = "C:\\Users\\vinot\\Documents\\jio_bill_screenshots\\topimg.png"; // Path to your image
            Image img = new Image(ImageDataFactory.create(imagePath));

            // Set image size and position
//            img.setFixedPosition(1, 0, 200); // x, y, width

            // Get the page size
            PageSize pageSize = pdfDoc.getDefaultPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();

            // Calculate scaling to fit the page width
            float originalWidth = img.getImageWidth();
            float originalHeight = img.getImageHeight();
            float scale = pageWidth / originalWidth;

            // Set the scaled image size
            float scaledWidth = originalWidth * scale;
            float scaledHeight = originalHeight * scale;
            img.setWidth(scaledWidth);
            img.setHeight(scaledHeight);

            // Calculate y position to place the image at the top
            float yPosition = pageHeight - scaledHeight;

            // Add the image to the first page (page number 1)
            img.setFixedPosition(1, 0, yPosition, scaledWidth);

            // Add the image to the document
            doc.add(img);

            String address = "Mr. Vinothgopi G\n" +
                    "4/1351-a 4/1351-a, ganapathy Illam\n" +
                    "Sivakasi, Hussain Colony 626130\n" +
                    "Virudhunagar Virudhunagar Tamilnadu India";

            Paragraph paragraph = new Paragraph(address);
            paragraph.setFontSize(8); // Set font size
            paragraph.setMarginTop(35); // Set top margin
            paragraph.setMarginLeft(5); // Set left margin

//            doc.add(paragraph);

            String details = " Jio Number : 914562359020\n" +
                    " Account Number : 410629241806\n" +
                    " Statement No. : 462505632985\n" +
                    " Statement Date : 12-Aug-2024\n" +
                    " Statement Time : 16:09:38";

            Paragraph detailsParagraph = new Paragraph(details);
            detailsParagraph.setFontSize(8); // Set font size
            detailsParagraph.setMarginTop(35); // Set top margin
            detailsParagraph.setMarginLeft(55); // Set left margin


            // Create a table with 2 columns
            Table table = new Table(3);
            table.setBorder(Border.NO_BORDER);

            // Add the first paragraph to the first cell
            Cell cell1 = new Cell().add(paragraph);
            cell1.setBorder(Border.NO_BORDER);
            table.addCell(cell1);

            Cell paymentQrCell = new Cell();
            paymentQrCell.setBorder(Border.NO_BORDER);
            String upiContent = "upi://pay?&ver=01&mode=16&orgid=700004&tr=S33I242504890704&tn=BILL PAYMENT FOR 11-JUL-2024 FOR S33I242504890704&pa=JIOC410629241806@HSBC&pn=RELIANCE RETAIL&mc=4900&am=4718.82&mid=TST5432&&&gst=719.82cgst:359.91|sgst:359.91&qrmedium=03&invoiceno=S33I242504890704&invoicedate=11-JUN-2024&InvoiceName=Mr. Vinothgopi G&&&gstin=33AABCI6363G1ZQ&&currency=INR";
            Image qrCodeImage = generateQRCodeImage(upiContent, 150, 150);
            qrCodeImage.scaleToFit(100,100);
            qrCodeImage.setMarginLeft(45);
            paymentQrCell.add(new Paragraph("\nScan & Pay").setFontSize(10)
                            .setMarginLeft(45)
                                .setTextAlignment(TextAlignment.CENTER));
            paymentQrCell.add(qrCodeImage.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER));
            String upiImg = "C:\\Users\\vinot\\Documents\\jio_bill_screenshots\\upi.png"; // Path to your image
            Image upiImage = new Image(ImageDataFactory.create(upiImg));
            upiImage.scaleToFit(85,55);
            upiImage.setMarginLeft(53);
            paymentQrCell.add(upiImage);
            table.addCell(paymentQrCell);

            // Add the second paragraph to the second cell
            detailsParagraph.setTextAlignment(TextAlignment.RIGHT);
            Cell cell2 = new Cell().add(detailsParagraph);
            cell2.setBorder(Border.NO_BORDER);
            table.addCell(cell2);

            doc.add(table);

            Paragraph summary = new Paragraph("Summary Account Payable Statement");
            summary.setBold();
            summary.setUnderline();
            summary.setFontSize(10);
            summary.setTextAlignment(TextAlignment.CENTER);
            doc.add(summary);

            Paragraph mobile = new Paragraph("Registered Mobile Number: +918760603355 || Email: vinothgopi@hotmail.com");
            doc.add(mobile);
            Paragraph statementDate = new Paragraph("Statement as on  12-Aug-2024");
            statementDate.setBold();
            doc.add(statementDate);

            SolidLine solidLine = new SolidLine(1f); // Create a solid line with thickness 1
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setWidth(UnitValue.createPercentValue(100)); // Set the width to 100% of the page width
            doc.add(lineSeparator);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static Image generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
//
//        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);  // Black for QR code, white for background
//            }
//        }
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
//        byte[] pngData = byteArrayOutputStream.toByteArray();
//        ImageData imageData = ImageDataFactory.create(pngData);
//        return new Image(imageData);
//    }

    public static Image generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        ImageData imageData = ImageDataFactory.create(pngData);
        return new Image(imageData);
    }
}
