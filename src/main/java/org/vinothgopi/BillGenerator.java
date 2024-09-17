//package org.vinothgopi;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.borders.SolidBorder;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.layout.property.HorizontalAlignment;
//import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.layout.property.UnitValue;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class BillGenerator {
//    public static void main(String[] args) throws WriterException {
//        String dest = "PDFWithImage.pdf"; // Output file
//
//        // Create a PdfWriter object
//        try (PdfWriter writer = new PdfWriter(dest);
//             PdfDocument pdfDoc = new PdfDocument(writer);
//             Document doc = new Document(pdfDoc)) {
//            // Load a font that supports the ₹ symbol
//            String fontPath = "src/main/resources/fonts/NotoSans-Regular.ttf"; // Replace with your font path
//            PdfFont font = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
//            doc.setFont(font);
//            doc.setFontSize(7);
//
//            // Load the image
//            String imagePath = "C:\\Users\\vinot\\Documents\\jio_bill_screenshots\\topimg.png"; // Path to your image
//            Image img = new Image(ImageDataFactory.create(imagePath));
//
//            // Set image size and position
////            img.setFixedPosition(1, 0, 200); // x, y, width
//
//            // Get the page size
//            PageSize pageSize = pdfDoc.getDefaultPageSize();
//            float pageWidth = pageSize.getWidth();
//            float pageHeight = pageSize.getHeight();
//
//            // Calculate scaling to fit the page width
//            float originalWidth = img.getImageWidth();
//            float originalHeight = img.getImageHeight();
//            float scale = pageWidth / originalWidth;
//
//            // Set the scaled image size
//            float scaledWidth = originalWidth * scale;
//            float scaledHeight = originalHeight * scale;
//            img.setWidth(scaledWidth);
//            img.setHeight(scaledHeight);
//
//            // Calculate y position to place the image at the top
//            float yPosition = pageHeight - scaledHeight;
//
//            // Add the image to the first page (page number 1)
//            img.setFixedPosition(1, 0, yPosition, scaledWidth);
//
//            // Add the image to the document
//            doc.add(img);
//
//            String address = "Mr. Vinothgopi G\n" +
//                    "4/1351-A, Hussain Colony 626189\n" +
//                    "Virudhunagar, Tamilnadu, India";
//
//            String jioNumber = "914562359020";
//            String accountNo = "410629241806";
//            String statementNo = "462505632985";
//            String date = "12-Aug-2024";
//            String gstBillNo = "S33I242507865046";
//
//
//            Paragraph paragraph = new Paragraph(address);
//            paragraph.setFixedLeading(12);
//            paragraph.setFontSize(8); // Set font size
//            paragraph.setMarginTop(35); // Set top margin
//            paragraph.setMarginLeft(5); // Set left margin
//
//            String details = " Jio Number : "+jioNumber+"\n" +
//                    " Account Number : "+accountNo+"\n" +
//                    " Statement No. : "+statementNo+"\n" +
//                    " Statement Date : "+date+"\n" +
//                    " Statement Time : 16:09:38";
//
//            String totalAmount = "4718.82";
//            double gstPercentage = 18.0;
//            double totalAmt = Double.parseDouble(totalAmount);
//            double gstAmt =  (totalAmt * gstPercentage) / ( 100 + gstPercentage);
//            double cgstAmt = gstAmt/2;
//            double sgstAmt = cgstAmt;
//
//            String totalGst = Double.toString(gstAmt);
//            String centralGst = Double.toString(cgstAmt);
//            String stateGst = Double.toString(sgstAmt);
//
//            Paragraph detailsParagraph = new Paragraph(details);
//            detailsParagraph.setFixedLeading(12);
//            detailsParagraph.setFontSize(8); // Set font size
//            detailsParagraph.setMarginTop(35); // Set top margin
//            detailsParagraph.setMarginLeft(75); // Set left margin
//
//
//            // Create a table with 2 columns
//            Table table = new Table(3);
//            table.setBorder(Border.NO_BORDER);
//
//            // Add the first paragraph to the first cell
//            Cell cell1 = new Cell().add(paragraph);
//            cell1.setBorder(Border.NO_BORDER);
//            table.addCell(cell1);
//
//            Cell paymentQrCell = new Cell();
//            paymentQrCell.setBorder(Border.NO_BORDER);
//            String upiContent = "upi://pay?&ver=01&mode=16&orgid=700004&tr="+gstBillNo+"&tn=BILL PAYMENT FOR "+date+" FOR "+gstBillNo+"&pa=JIOC410629241806@HSBC&pn=RELIANCE RETAIL&mc=4900&am="+totalAmount+"&mid=TST5432&&&gst=719.82cgst:359.91|sgst:359.91&qrmedium=03&invoiceno="+gstBillNo+"&invoicedate="+date+"&InvoiceName=Mr. Vinothgopi G&&&gstin=33AABCI6363G1ZQ&&currency=INR";
//            Image qrCodeImage = generateQRCodeImage(upiContent, 150, 150);
//            qrCodeImage.scaleToFit(80,80);
//            qrCodeImage.setMarginLeft(45);
//            paymentQrCell.add(new Paragraph("\nScan & Pay").setFontSize(7)
//                            .setMarginLeft(45)
//                            .setTextAlignment(TextAlignment.CENTER));
//            paymentQrCell.add(qrCodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER));
//            String upiImg = "C:\\Users\\vinot\\Documents\\jio_bill_screenshots\\upi.png"; // Path to your image
//            Image upiImage = new Image(ImageDataFactory.create(upiImg));
//            upiImage.scaleToFit(75,50);
//            upiImage.setMarginLeft(50);
//            paymentQrCell.add(upiImage);
//            table.addCell(paymentQrCell);
//
//            // Add the second paragraph to the second cell
//            detailsParagraph.setTextAlignment(TextAlignment.RIGHT);
//            Cell cell2 = new Cell().add(detailsParagraph);
//            cell2.setBorder(Border.NO_BORDER);
//            table.addCell(cell2);
//
//            doc.add(table);
//
//            Paragraph summary = new Paragraph("Summary Account Payable Statement");
//            summary.setBold();
//            summary.setUnderline();
//            summary.setFontSize(10);
//            summary.setTextAlignment(TextAlignment.CENTER);
//            doc.add(summary);
//
//            Paragraph mobile = new Paragraph("Registered Mobile Number: +918760603355 || Email: vinothgopi@hotmail.com");
//            mobile.setFontSize(9);
//            doc.add(mobile);
//
//            Table table2 = new Table(2);
//            Cell cell11 = new Cell();
//            cell11.setBorder(Border.NO_BORDER);
//            Paragraph statementDate = new Paragraph("Statement as on 12-Aug-2024");
//            statementDate.setFontSize(8);
//            statementDate.setBold();
//            cell11.add(statementDate);
//            table2.addCell(cell11);
//
//            Cell cell12 = new Cell();
//            cell12.setBorder(Border.NO_BORDER);
//            Paragraph jioFiber = new Paragraph("Jio Fiber");
//            jioFiber.setFontSize(7);
//            jioFiber.setMarginLeft(360);
//            cell12.add(jioFiber);
//            table2.addCell(cell12);
//
//            doc.add(table2);
//
//            SolidLine solidLine = new SolidLine(1f); // Create a solid line with thickness 1
//            LineSeparator lineSeparator = new LineSeparator(solidLine);
//            lineSeparator.setWidth(UnitValue.createPercentValue(100)); // Set the width to 100% of the page width
//            lineSeparator.setMarginBottom(10);
//            doc.add(lineSeparator);
//
//            Table billTable = new Table(3);
//
//            Cell billCell1 = new Cell();
//            Paragraph paragraph1 = new Paragraph("Particulars").setMarginLeft(10);
//            paragraph1.setFontSize(9);
//            billCell1.setBorder(Border.NO_BORDER);
//            paragraph1.setBold();
//            billCell1.add(paragraph1);
//
//            billTable.addCell(billCell1);
//
//            Cell invoiceCell = new Cell();
//            invoiceCell.setBorder(Border.NO_BORDER);
//            Paragraph invoiceNumber = new Paragraph("Invoice Number");
//            invoiceNumber.setFontSize(9);
//            invoiceNumber.setMarginLeft(100);
//            invoiceNumber.setBold();
//            invoiceCell.add(invoiceNumber);
//
//            billTable.addCell(invoiceCell);
//
//            Cell amountCell = new Cell();
//            amountCell.setBorder(Border.NO_BORDER);
//            Paragraph amountPara = new Paragraph("Amount (₹)");
//            amountPara.setFontSize(9);
//            amountPara.setFont(font);
//            amountPara.setMarginLeft(150);
//            amountPara.setBold();
//            amountCell.add(amountPara);
//
//            billTable.addCell(amountCell);
//
//            billTable.addCell(new Cell().add(new Paragraph("(i) Connectivity Services").setMarginLeft(10).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            billTable.setBorder(Border.NO_BORDER);
//
//            // Add a border to the table only
//            billTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));
//
//            billTable.addCell(new Cell().add(new Paragraph("Previous Balance Due").setMarginLeft(10).setFixedLeading(5).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(5).setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            int leading = 15;
//            billTable.addCell(new Cell().add(new Paragraph("Payment Received").setMarginLeft(10).setFixedLeading(leading).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(leading).setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Current Month Payable").setMarginLeft(10).setFixedLeading(5).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("444503240343").setFixedLeading(5).setMarginLeft(110).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(5).setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            int leading2 = 15;
//            billTable.addCell(new Cell().add(new Paragraph("Total (i)").setMarginLeft(10).setFixedLeading(leading2).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(leading2).setBold().setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("(ii) Platform Services").setMarginLeft(10).setFixedLeading(5).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Previous Balance Due").setMarginLeft(10).setFixedLeading(leading2).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setMarginLeft(170).setFontSize(8).setFixedLeading(leading2)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Payment Received").setMarginLeft(10).setFixedLeading(5).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(5).setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Current Month Payable").setMarginLeft(10).setFixedLeading(leading2).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("444503240343").setFixedLeading(5).setMarginLeft(110).setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(leading2).setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Total (ii)").setMarginLeft(10).setFixedLeading(5).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("582.92").setFixedLeading(5).setBold().setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Total current charges (i + ii)").setMarginLeft(10).setFixedLeading(leading2).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph(totalAmount).setFixedLeading(leading2).setBold().setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("(iii) Previous Balance with RRL").setMarginLeft(10).setFixedLeading(leading2).setFontSize(8).setWidth(120)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph("0").setFixedLeading(leading2).setBold().setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            billTable.addCell(new Cell().add(new Paragraph("Total Amount Payable (i + ii + iii)").setMarginLeft(10).setFixedLeading(leading2).setBold().setFontSize(8)).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().setMarginLeft(200).setBorder(Border.NO_BORDER));
//            billTable.addCell(new Cell().add(new Paragraph(totalAmount).setFixedLeading(leading2).setBold().setMarginLeft(170).setFontSize(8)).setBorder(Border.NO_BORDER));
//
//            doc.add(billTable);
//
//            Table payByTable = new Table(1);
//            payByTable.setMarginTop(30);
//            payByTable.setMarginLeft(200);
//            payByTable.addCell(new Cell().add(new Paragraph("Pay By 20-SEP-2024\n" +
//                    "₹706.82").setTextAlignment(TextAlignment.CENTER).setBold().setMarginLeft(30).setMarginRight(30).setFontSize(9)));
//
//            doc.add(payByTable);
//
//            String imagePath1 = "C:\\Users\\vinot\\Documents\\jio_bill_screenshots\\bottom.png"; // Path to your image
//            Image img1 = new Image(ImageDataFactory.create(imagePath1));
//
//            // Get the page size
//            PageSize pageSize1 = pdfDoc.getDefaultPageSize();
//            float pageWidth1 = pageSize1.getWidth();
//            float pageHeight1 = pageSize1.getHeight();
//
//            // Calculate scaling to fit the page width
//            float originalWidth1 = img1.getImageWidth();
//            float originalHeight1 = img1.getImageHeight();
//            float scale1 = pageWidth1 / originalWidth1;
//
//            // Set the scaled image size
//            float scaledWidth1 = originalWidth1 * scale1;
//            float scaledHeight1 = originalHeight1 * scale1;
//            img1.setWidth(scaledWidth1);
//            img1.setHeight(scaledHeight1);
//
//            img1.setFixedPosition(0, 0);
//            // Add the image to the document
//            doc.add(img1);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    private static Image generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
////        QRCodeWriter qrCodeWriter = new QRCodeWriter();
////        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
////
////        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
////        for (int x = 0; x < width; x++) {
////            for (int y = 0; y < height; y++) {
////                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);  // Black for QR code, white for background
////            }
////        }
////
////        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
////        byte[] pngData = byteArrayOutputStream.toByteArray();
////        ImageData imageData = ImageDataFactory.create(pngData);
////        return new Image(imageData);
////    }
//
//    public static Image generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//
//        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//        byte[] pngData = pngOutputStream.toByteArray();
//
//        ImageData imageData = ImageDataFactory.create(pngData);
//        return new Image(imageData);
//    }
//}
