package org.vinothgopi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class BillGeneratorLatest {

    static Properties prop = new Properties();

    static Config config = loadConfig();


    static String address = config.getAddress();
//            "Mr. Vinothgopi G\n" +
//            "4/1351-A, Hussain Colony 626189\n" +
//            "Virudhunagar, Tamilnadu, India\n\n\n\n\n";

    static String placeOfSupply = config.getPlaceOfSupply();

    static String jioNumber = config.getJioNumber();
    static String accountNo = config.getAccountNo();
    static String statementNo = config.getStatementNo();
    static String billingDate = config.getBillingDate();
    static String dueDate = config.getDueDate();
    static String gstBillNo = config.getGstBillNo();
    static String registeredMobile = config.getRegisteredMobile();
    static String email = config.getEmail();
    static String period = config.getPeriod();

    static String totalAmount = config.getTotalAmount();
    static double gstPercentage = config.getGstPercentage();
    static double totalAmt = Double.parseDouble(totalAmount);
    static double gstAmt =  (totalAmt * gstPercentage) / ( 100 + gstPercentage);
    static double cgstAmt = gstAmt/2;
    static double sgstAmt = cgstAmt;
    static double currentTaxable = totalAmt - gstAmt;

    static String totalGst = String.format("%.2f", gstAmt);
    static String centralGst = String.format("%.2f", cgstAmt);
    static String stateGst =  String.format("%.2f", sgstAmt);

    static String rupeeSymbol = "\u20B9";

    public BillGeneratorLatest() throws FileNotFoundException {
    }

    public static void addOvals(PdfDocument pdf, int x, int y, String[] textAbove, String belowText) throws IOException {
//        pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());

        // Set the rectangle bounds for the oval (x, y, width, height)
        Rectangle ovalBounds = new Rectangle(x, y, 78, 68);

        // Set the stroke color (optional)
        canvas.setStrokeColor(ColorConstants.BLACK);

        // Draw the oval (ellipse)
        canvas.ellipse(ovalBounds.getLeft(), ovalBounds.getBottom(),
                ovalBounds.getRight(), ovalBounds.getTop());
        canvas.stroke();

        // Draw the line splitting the oval (from top center to bottom center)
        float centerY = ovalBounds.getBottom() + ovalBounds.getHeight() / 2;
        canvas.moveTo(ovalBounds.getLeft(), centerY);
        canvas.lineTo(ovalBounds.getRight(), centerY);
        canvas.stroke();

        // Create a font for text
        PdfFont font =getFont();

        // Set text color and font size
        canvas.setFontAndSize(font, 8);
        canvas.setFillColor(ColorConstants.BLACK);

//        String textAbove = "My Previous \n" +
//                "Balance ("+rupeeSymbol+")";
//        float textAboveWidth = font.getWidth(aboveText, 6);
//        float textAboveX = ovalBounds.getLeft() + (ovalBounds.getWidth() - textAboveWidth) / 2;
//        float textAboveY = centerY + 5; // Adjust distance above the line
//
//        // Show text above the line
//        canvas.beginText();
//        canvas.moveText(textAboveX, textAboveY);
//        canvas.showText(aboveText);
//        canvas.endText();
//        String[] textAbove = {"Payment", "Received ("+rupeeSymbol+")"};
        float lineHeight = 8; // Space between lines

        for (int i = 0; i < textAbove.length; i++) {
            float textAboveWidth = font.getWidth(textAbove[i], 8);
            float textAboveX = ovalBounds.getLeft() + (ovalBounds.getWidth() - textAboveWidth) / 2;
            float textAboveY = centerY + 2 + (lineHeight * (textAbove.length - i)); // Adjust Y for each line

            // Show each line of text above
            canvas.beginText();
            canvas.moveText(textAboveX, textAboveY);
            canvas.showText(textAbove[i]);
            canvas.endText();
        }

        // Text below the line
//        String textBelow = "0.00";
        float textBelowWidth = font.getWidth(belowText, 6);
        float textBelowX = ovalBounds.getLeft() + (ovalBounds.getWidth() - textBelowWidth) / 2;
        float textBelowY = centerY - 10; // Adjust distance below the line

        // Show text below the line
        canvas.beginText();
        canvas.moveText(textBelowX, textBelowY);
        canvas.showText(belowText);
        canvas.endText();
    }

    private static Cell getEmptyCell() {
        return new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(50);
    }

    private static Cell getCell(String val) {
        return new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(val).setFixedLeading(5)).setPaddingLeft(10);
    }

    private static void addBillTable(Document doc, PdfDocument pdfDoc) throws MalformedURLException {
        Table table = new Table(5);
        table.useAllAvailableWidth();
        table.setMarginTop(85);
        table.setBorder(Border.NO_BORDER);

        // Add a border to the table only
        table.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Amount ("+rupeeSymbol+")").setFixedLeading(5)).setPaddingLeft(10));

        table.addCell(getCell("1"));
        table.addCell(getCell("Monthly Plan Charges"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(String.valueOf(currentTaxable)).setFixedLeading(5)).setPaddingLeft(10));

        table.addCell(getCell("2"));
        table.addCell(getCell("Usage Charges"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());


        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("Volume / Duration"));
        table.addCell(getCell("Amount"));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("ISD / Premium Calling"));
        table.addCell(getCell("00:00:00"));
        table.addCell(getCell("0.00"));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("Data"));
        table.addCell(getCell("0.000 GB"));
        table.addCell(getCell("0.00"));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("VAS"));
        table.addCell(getCell("0"));
        table.addCell(new Cell().add(new Paragraph("0.00").setFixedLeading(5).setUnderline()).setBorder(Border.NO_BORDER).setPaddingLeft(10));
        table.addCell(getCell("0.00"));

        table.addCell(getCell("3"));
        table.addCell(getCell("Other Plan Charges"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        Table subTable = new Table(4);
        subTable.setBorder(Border.NO_BORDER);
        subTable.useAllAvailableWidth();
        // Add a border to the table only
        subTable.setBorder(new DottedBorder(new DeviceRgb(0, 0, 0), 1));

        subTable.addCell(new Cell().add(new Paragraph("Total Usage Summary").setFixedLeading(5).setPaddingLeft(10)
                .setBold().setUnderline().setFontSize(8)).setBorder(Border.NO_BORDER));
        subTable.addCell(getEmptyCell());
        subTable.addCell(getEmptyCell());
        subTable.addCell(getEmptyCell());

        subTable.addCell(getEmptyCell());
        subTable.addCell(getCell("Total Usage"));
        subTable.addCell(getCell("Chargeable Usage"));
        subTable.addCell(getCell("Charged Amount"));

        subTable.addCell(getCell("Voice Local"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("0.00"));

        subTable.addCell(getCell("Voice STD"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("0.00"));

        subTable.addCell(getCell("ISD / Premium Calling"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("00:00:0"));
        subTable.addCell(getCell("0.00"));

        subTable.addCell(getCell("Data"));
        subTable.addCell(getCell("652.358 GB"));
        subTable.addCell(getCell("0.000 GB"));
        subTable.addCell(getCell("0.00"));

        subTable.addCell(getCell("VAS"));
        subTable.addCell(getCell("0"));
        subTable.addCell(getCell("0"));
        subTable.addCell(getCell("0.00"));



        // Apply a dotted border to the table itself
        subTable.setBorder(new DashedBorder(new DeviceRgb(0, 0, 0), 1));
        table.addCell(getEmptyCell());
        table.addCell(new Cell(1,3).setBorder(Border.NO_BORDER).add(subTable));

        table.addCell(getEmptyCell());

        table.addCell(getCell("4"));
        table.addCell(getCell("One Time Charges"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));

        table.addCell(new Cell().add(new Paragraph("5").setBold()).setBorder(Border.NO_BORDER).setPaddingLeft(10));
        table.addCell(new Cell().add(new Paragraph("Total value of charges (1+2+3+4)").setBold().setFixedLeading(5)).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(new Cell().add(new Paragraph(String.valueOf(currentTaxable)).setBold().setFixedLeading(5).setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));

        table.addCell(getCell("6"));
        table.addCell(getCell("Current Month Discount/ Credit/ Debit"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("Current Month's Discount"));
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("Current Month's Adjustments"));
        table.addCell(getEmptyCell());
        table.addCell(new Cell().add(new Paragraph("0.00").setFixedLeading(5).setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(getEmptyCell());

        table.addCell(new Cell().add(new Paragraph("7").setFixedLeading(5).setBold()).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph("Charges (5+6)").setFixedLeading(5).setBold()).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(new Cell().add(new Paragraph(String.valueOf(currentTaxable)).setFixedLeading(5).setBold().setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));

        table.addCell(getCell("8"));
        table.addCell(getCell("Taxes"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("CGST (9%)"));
        table.addCell(getEmptyCell());
        table.addCell(getCell(centralGst));
        table.addCell(getEmptyCell());

        table.addCell(getEmptyCell());
        table.addCell(getCell("SGST (9%)"));
        table.addCell(getEmptyCell());
        table.addCell(new Cell().add(new Paragraph(stateGst).setFixedLeading(5).setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(totalGst).setFixedLeading(5).setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));

        table.addCell(getCell("9"));
        table.addCell(getCell("Bill Discount Including Tax"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));

        table.addCell(getCell("10"));
        table.addCell(getCell("Security Deposit Returned"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));

        table.addCell(getCell("11"));
        table.addCell(getCell("Pre to Post Migration Balance Transfer"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));

        table.addCell(getCell("12"));
        table.addCell(getCell("Waivers"));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(getCell("0.00"));

        table.addCell(getEmptyCell());
//        table.addCell(getCell("Current Month Charges (7+8+9+10+11+12)"));
        table.addCell(new Cell().add(new Paragraph("Current Month Charges (7+8+9+10+11+12)").setFixedLeading(5).setBold().setUnderline()).setPaddingLeft(10).setBorder(Border.NO_BORDER));
        table.addCell(getEmptyCell());
        table.addCell(getEmptyCell());
        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalAmt)).setBold().setFixedLeading(5).setUnderline().setMarginBottom(5)).setPaddingLeft(10).setBorder(Border.NO_BORDER));
//        table.addCell(getCell("582.92"));
        doc.add(table);

    }

    private static PdfFont getFont() throws IOException {
        InputStream fontStream = BillGeneratorLatest.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf");

        if (fontStream == null) {
            throw new RuntimeException("Font not found in resources folder!");
        }

        // Create font using the font stream
        PdfFont font = PdfFontFactory.createFont(fontStream.readAllBytes(), PdfEncodings.IDENTITY_H, true);
        return font;
    }

    private static void addJioDigitalLifeImage(Document doc, PdfDocument pdfDoc) throws MalformedURLException {
        String imagePath = "src/main/resources/images/topimg.png"; // Path to your image
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
    }


    public static Config loadConfig() {
        Properties prop = new Properties();
        Config config = new Config();

        try (InputStream input = BillGeneratorLatest.class.getClassLoader().getResourceAsStream("input.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }

            // Load the properties file
            prop.load(input);

            // Map properties to POJO
            config.setAddress(prop.getProperty("address"));
            config.setPlaceOfSupply(prop.getProperty("placeOfSupply"));
            config.setJioNumber(prop.getProperty("jioNumber"));
            config.setAccountNo(prop.getProperty("accountNo"));
            config.setStatementNo(prop.getProperty("statementNo"));
            config.setBillingDate(prop.getProperty("billingDate"));
            config.setDueDate(prop.getProperty("dueDate"));
            config.setGstBillNo(prop.getProperty("gstBillNo"));
            config.setRegisteredMobile(prop.getProperty("registeredMobile"));
            config.setEmail(prop.getProperty("email"));
            config.setPeriod(prop.getProperty("period"));
            config.setTotalAmount(prop.getProperty("totalAmount"));
            config.setGstPercentage(Double.parseDouble(prop.getProperty("gstPercentage")));
            config.setFileName(prop.getProperty("fileName"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return config;
    }

    public static void main(String[] args) throws WriterException {

        try (InputStream input =
                     BillGeneratorLatest.class.getClassLoader().getResourceAsStream("input1.properties")) {
            prop.load(input);
            prop.entrySet().stream().forEach(System.out::println);
        } catch(Exception e) {
            e.printStackTrace();
        }

        address = prop.getProperty("address");
        placeOfSupply = prop.getProperty("placeOfSupply");
        jioNumber = prop.getProperty("jioNumber");
        accountNo = prop.getProperty("accountNo");
        statementNo = prop.getProperty("statementNo");
        billingDate = prop.getProperty("billingDate");
        dueDate = prop.getProperty("dueDate");
        gstBillNo = prop.getProperty("gstBillNo");
        registeredMobile = prop.getProperty("registeredMobile");
        email = prop.getProperty("email");
        period = prop.getProperty("period");
        totalAmount = prop.getProperty("totalAmount");
        gstPercentage = Double.parseDouble(prop.getProperty("gstPercentage"));

        String dest = config.getFileName(); // Output file

        // Create a PdfWriter object
        try (PdfWriter writer = new PdfWriter(dest);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {
            // Load a font that supports the "+rupeeSymbol+" symbol

//            String fontPath = "src/main/resources/fonts/NotoSans-Regular.ttf"; // Replace with your font path
            String fontPath = "src/main/resources/fonts/DejaVuSans.ttf"; // Replace with your font path
//            PdfFont font = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
//            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            PdfFont font = getFont();



            doc.setFont(font);
            doc.setFontSize(7);

            // Load the image
            addJioDigitalLifeImage(doc, pdfDoc);

            Paragraph placePara = new Paragraph(placeOfSupply).setFixedLeading(10);
            placePara.setFontSize(8);

            addOvals(pdfDoc, 30,560, new String[]{"My Previous", "Balance ("+rupeeSymbol+")"}, "0.00");
            addOvals(pdfDoc, 120,560, new String[]{"Payment", "Received ("+rupeeSymbol+")"}, "0.00");
            addOvals(pdfDoc, 205,560, new String[]{"Current Taxable", "Charges ("+rupeeSymbol+")"}, String.valueOf(currentTaxable));
            addOvals(pdfDoc, 290,560, new String[]{"Taxes ("+rupeeSymbol+")"}, String.format("%.2f", gstAmt) );
            addOvals(pdfDoc, 375,560, new String[]{" Total Current ", "Month Charges ("+rupeeSymbol+")"}, totalAmount);
            addOvals(pdfDoc, 460,560, new String[]{" Total Payable ", "("+rupeeSymbol+")"}, totalAmount);
            Paragraph paragraph = new Paragraph(address);
            paragraph.setWidth(150);
            paragraph.setFixedLeading(12);
            paragraph.setFontSize(8); // Set font size
            paragraph.setMarginTop(10); // Set top margin
            paragraph.setMarginLeft(2); // Set left margin

            String details = " Jio Number            : "+jioNumber+"\n" +
                    " Account Number        : "+accountNo+"\n" +
                    " GST Bill Number       : "+gstBillNo+"\n" +
                    " Document Number       : "+statementNo+"\n" +
                    " Billing cycle Date    : "+billingDate+"\n" +
                    " Due Date              : "+dueDate+"\n" +
                    " Credit Limit          : "+rupeeSymbol+"0\n" +
                    " Security Deposit      : "+rupeeSymbol+"0\n" ;

            Table detailsTable = new Table(3);
            detailsTable.setMarginLeft(50);
            detailsTable.setBorder(Border.NO_BORDER);
            detailsTable.addCell(new Cell().add(new Paragraph("Jio Number").setFixedLeading(6).setTextAlignment(TextAlignment.LEFT).setWidth(100)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(6)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(jioNumber).setFixedLeading(6)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("Account Number").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(accountNo).setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("GST Bill Number").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(gstBillNo).setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("Document Number").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(statementNo).setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("Billing cycle Date").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(billingDate).setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("Credit Limit").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(""+rupeeSymbol+"0").setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            detailsTable.addCell(new Cell().add(new Paragraph("Security Deposit").setFixedLeading(5)).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(":").setFixedLeading(5)).setBorder(Border.NO_BORDER));
            detailsTable.addCell(new Cell().add(new Paragraph(""+rupeeSymbol+"0").setFixedLeading(5)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));






            Paragraph originForReceipt = new Paragraph("Original for Recipient").setTextAlignment(TextAlignment.CENTER).setFixedLeading(8);
            originForReceipt.setFontSize(8); // Set font size
            originForReceipt.setMarginTop(10); // Set top margin
            originForReceipt.setMarginLeft(55); // Set left margin
            Paragraph detailsParagraph = new Paragraph(details);
            detailsParagraph.setFixedLeading(10);
            detailsParagraph.setFontSize(8); // Set font size
//            detailsParagraph.setMarginTop(35); // Set top margin
            detailsParagraph.setMarginLeft(45); // Set left margin


            // Create a table with 2 columns
            Table table = new Table(3);
            table.setBorder(Border.NO_BORDER);

            // Add the first paragraph to the first cell
            Cell cell1 = new Cell().add(paragraph).add(placePara);
            cell1.setBorder(Border.NO_BORDER);
            table.addCell(cell1);

            Cell paymentQrCell = new Cell();
            paymentQrCell.setBorder(Border.NO_BORDER);
            String upiContent = "upi://pay?&ver=01&mode=16&orgid=700004&tr="+gstBillNo+"&tn=BILL PAYMENT FOR "+billingDate+" FOR "+gstBillNo+"&pa=JIOC410629241806@HSBC&pn=RELIANCE RETAIL&mc=4900&am="+totalAmount+"&mid=TST5432&&&gst=719.82cgst:359.91|sgst:359.91&qrmedium=03&invoiceno="+gstBillNo+"&invoicedate="+billingDate+"&InvoiceName=Mr. Vinothgopi G&&&gstin=33AABCI6363G1ZQ&&currency=INR";
            Image qrCodeImage = generateQRCodeImage(upiContent, 150, 150);
            qrCodeImage.scaleToFit(100,100);
            qrCodeImage.setMarginLeft(50);
            qrCodeImage.setMarginTop(8);
//            paymentQrCell.add(new Paragraph("\nScan & Pay").setFontSize(7)
//                            .setMarginLeft(45)
//                            .setTextAlignment(TextAlignment.CENTER));
            paymentQrCell.add(qrCodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER));
            String upiImg = "src/main/resources/images/upi.png"; // Path to your image
            Image upiImage = new Image(ImageDataFactory.create(upiImg));
            upiImage.scaleToFit(60,50);
            upiImage.setMarginLeft(70);
            paymentQrCell.add(upiImage);
            table.addCell(paymentQrCell);

            // Add the second paragraph to the second cell
            detailsParagraph.setTextAlignment(TextAlignment.JUSTIFIED_ALL);
//            Cell cell2 = new Cell().add(originForReceipt).add(detailsParagraph);
            Cell cell2 = new Cell().add(originForReceipt).add(detailsTable);
            cell2.setBorder(Border.NO_BORDER);
            table.addCell(cell2);

            doc.add(table);

            Paragraph aadhaar = new Paragraph("Registered Mobile : "+ registeredMobile +" | Aadhaar Number : XXXX XXXX1978 | E-Mail: "+email);
            aadhaar.setFontSize(8);
            aadhaar.setFixedLeading(5);
            doc.add(aadhaar);

            Paragraph summary = new Paragraph("Connectivity Services");
            summary.setBold();
            summary.setUnderline();
            summary.setFontSize(10);
            summary.setFixedLeading(10);
            summary.setTextAlignment(TextAlignment.CENTER);
            doc.add(summary);

//            Paragraph mobile = new Paragraph("Registered Mobile Number: +918760603355 || Email: vinothgopi@hotmail.com");
//            mobile.setFontSize(9);
//            doc.add(mobile);

            Table table2 = new Table(2);
            Cell cell11 = new Cell();
            cell11.setBorder(Border.NO_BORDER);
            Paragraph statementDate = new Paragraph("Your Jio Fiber Bill From " + period).setFixedLeading(6);
            statementDate.setWidth(250);
            statementDate.setFontSize(9);
            statementDate.setBold();
            cell11.add(statementDate);
            cell11.setWidth(UnitValue.createPercentValue(70));
            table2.addCell(cell11);

            Cell cell12 = new Cell();
            cell12.setBorder(Border.NO_BORDER);
            cell12.setVerticalAlignment(VerticalAlignment.BOTTOM);
            Paragraph jioFiber = new Paragraph("Refer following pages for details of charges");
            jioFiber.setFontSize(5);
            jioFiber.setMarginLeft(40);
            jioFiber.setVerticalAlignment(VerticalAlignment.BOTTOM);
            cell12.add(jioFiber);
            table2.addCell(cell12);

            doc.add(table2);

            SolidLine solidLine = new SolidLine(1f); // Create a solid line with thickness 1
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setWidth(UnitValue.createPercentValue(100)); // Set the width to 100% of the page width
            lineSeparator.setMarginBottom(10);
            doc.add(lineSeparator);

            addBillTable(doc, pdfDoc);

            String imagePath1 = "src/main/resources/images/payment1.png"; // Path to your image
            Image img1 = new Image(ImageDataFactory.create(imagePath1));

            // Get the page size
            PageSize pageSize1 = pdfDoc.getDefaultPageSize();
            float pageWidth1 = pageSize1.getWidth();
            float pageHeight1 = pageSize1.getHeight();

            // Calculate scaling to fit the page width
            float originalWidth1 = img1.getImageWidth();
            float originalHeight1 = img1.getImageHeight();
            float scale1 = pageWidth1 / originalWidth1;

            // Set the scaled image size
            float scaledWidth1 = originalWidth1 * scale1;
            float scaledHeight1 = originalHeight1 * scale1;
            img1.setWidth(scaledWidth1);
            img1.setHeight(scaledHeight1);

            img1.setFixedPosition(0, 0);
            // Add the image to the document
            doc.add(img1);

            String qrText = "{\"SELLERGSTIN\":\"33AABCI6363G1ZQ\",\"PLACEOFSUPPLY\":\"33\"," +
                    "\"DOCNO\":"+gstBillNo+",\"DOCTYP\":\"INV\",\"DOCDT\":"+billingDate+"," +
                    "\"TOTINVVAL\":"+totalAmount+",\"ITEMCNT\":\"1\",\"MAINHSNCODE\":\"998413\"}";
            BarcodeQRCode qrCode = new BarcodeQRCode(qrText);
            Image qrImg = new Image(qrCode.createFormXObject(pdfDoc));
//            Image qrImg = generateQRCodeImage(qrText, 150, 150);
            qrImg.setMargins(0,0,0,0);
            qrImg.setFixedPosition(425,25 );
//            qrImg.setWidth(UnitValue.createPercentValue(100));
            qrImg.scaleToFit(170,170);
//            qrImg.setMarginLeft(400);
//            qrImg.setMarginTop(20);
//            paymentQrCell.add(new Paragraph("\nScan & Pay").setFontSize(7)
//                            .setMarginLeft(45)
//                            .setTextAlignment(TextAlignment.CENTER));
//            paymentQrCell.add(qrImg.setHorizontalAlignment(HorizontalAlignment.CENTER));

            doc.add(qrImg);

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
