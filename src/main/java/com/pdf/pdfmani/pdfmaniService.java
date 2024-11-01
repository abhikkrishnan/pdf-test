package com.pdf.pdfmani;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;


import javafx.scene.paint.Color;


@Service
public class pdfmaniService {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 14;
    private static final int TITLE_FONT_SIZE = 16;
    private static final int FONT_SIZE = 12;

    public byte[] generatePdf() throws IOException {
        try (PDDocument document = new PDDocument()) {
            // Create a blank page
            PDPage page = new PDPage();
            document.addPage(page);

            // Content stream to write content to the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello, PDFBox in Spring Boot!");
                contentStream.endText();
            }

            // Save PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generatePdfWithTitleAndParagraphs(String title, List<String> paragraphs) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);

                // Title
                float startY = page.getMediaBox().getHeight() - MARGIN;
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, startY);
                contentStream.showText(title);
                contentStream.endText();

                // Move down after title
                float currentY = startY - (2 * LINE_HEIGHT);

                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);

                // Handle each paragraph
                for (String paragraph : paragraphs) {
                    List<String> lines = wrapTextToWidth(paragraph, PDType1Font.HELVETICA, FONT_SIZE, page.getMediaBox().getWidth() - 2 * MARGIN);

                    for (String line : lines) {
                        if (currentY < MARGIN + LINE_HEIGHT) {
                            // Close current content stream and add a new page if not enough space
                            contentStream.close();

                            page = new PDPage();
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                            currentY = page.getMediaBox().getHeight() - MARGIN;
                        }

                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, currentY);
                        contentStream.showText(line);
                        contentStream.endText();
                        currentY -= LINE_HEIGHT;
                    }

                    // Add space between paragraphs
                    currentY -= LINE_HEIGHT;
                }
            } finally {
                contentStream.close();
            }

            // Save PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Utility method to wrap text to the specified width.
     */
    private List<String> wrapTextToWidth(String text, PDType1Font font, int fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + (line.length() > 0 ? " " : "") + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line.append((line.length() > 0 ? " " : "")).append(word);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }  

    public byte[] generatePdf_merged() throws IOException {
        // Path to your existing PDF file
        String existingPdfPath = "src/main/resources/existing.pdf";
        
        try (PDDocument document = new PDDocument()) {
            // Create a blank page
            PDPage page = new PDPage();
            document.addPage(page);
    
            // Content stream to write content to the blank page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello, PDFBox in Spring Boot!");
                contentStream.endText();
            }
    
            // Load the existing PDF
            try (PDDocument existingDocument = PDDocument.load(new File(existingPdfPath))) {
                PDFMergerUtility merger = new PDFMergerUtility();
                merger.appendDocument(document, existingDocument);
            }
    
            // Save PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }    

    public byte[] generatePdfWithSpecificPage(int pageIndex) throws IOException {
        // Path to your existing PDF file
        String existingPdfPath = "src/main/resources/existing.pdf";
        
        try (PDDocument document = new PDDocument();
             PDDocument existingDocument = PDDocument.load(new File(existingPdfPath))) {
            

                
    
            // Get the specific page from the existing document
            if (pageIndex < existingDocument.getNumberOfPages()) {
                PDPage specificPage = existingDocument.getPage(pageIndex);
                document.addPage(specificPage);
            } else {
                throw new IllegalArgumentException("Invalid page index: " + pageIndex);
            }
    
            // Save PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public byte[] mergePdfs() throws IOException {
        try (PDDocument mergedDocument = new PDDocument();
             PDDocument firstDocument = PDDocument.load(new File("src/main/resources/existing.pdf"));
             PDDocument secondDocument = PDDocument.load(new File("src/main/resources/lanscape.pdf"))) {
            
            // Add all pages from the first document to the merged document
            for (PDPage page : firstDocument.getPages()) {
                mergedDocument.addPage(page);
            }
    
            // Add all pages from the second document to the merged document
            for (PDPage page : secondDocument.getPages()) {
                mergedDocument.addPage(page);
            }
    
            // Save the merged PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mergedDocument.save(baos);
            return baos.toByteArray();
        }
    }


    public byte[] resizePdf(PDRectangle newSize) throws IOException {
        try (PDDocument document = PDDocument.load(new File("src/main/resources/existing.pdf"))) {
            PDDocument newDocument = new PDDocument();
            PDFRenderer renderer = new PDFRenderer(document);
            
            // Set a high DPI value for better quality (e.g., 300 DPI)
            final float targetDpi = 300;
    
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage newPage = new PDPage(newSize);
                newDocument.addPage(newPage);
    
                // Render the original page to a high-resolution BufferedImage
                BufferedImage image = renderer.renderImageWithDPI(i, targetDpi);
    
                // Create a PDImageXObject from BufferedImage using LosslessFactory
                PDImageXObject pdImage = LosslessFactory.createFromImage(newDocument, image);
    
                // Fit image to new page size without cropping
                try (PDPageContentStream contentStream = new PDPageContentStream(newDocument, newPage)) {
                    contentStream.drawImage(pdImage, 0, 0, newSize.getWidth(), newSize.getHeight());
                }
            }
    
            // Save the resized PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newDocument.save(baos);
            newDocument.close();
            return baos.toByteArray();
        }
    }

    public byte[] addMarginToPdf(float marginSize) throws IOException {
        marginSize*=72;
        try (PDDocument document = PDDocument.load(new File("src/main/resources/existing.pdf"))) {
            PDDocument newDocument = new PDDocument();
            PDFRenderer renderer = new PDFRenderer(document);
    
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage originalPage = document.getPage(i);
                PDRectangle originalSize = originalPage.getMediaBox();
    
                // Create a new page with the same original size
                PDPage newPage = new PDPage(originalSize);
                newDocument.addPage(newPage);
    
                // Render the original page to a high-resolution BufferedImage
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
    
                // Create a PDImageXObject from BufferedImage using LosslessFactory
                PDImageXObject pdImage = LosslessFactory.createFromImage(newDocument, image);
    
                // Calculate the scale factor to fit within the margin
                float scale = Math.min(
                    (originalSize.getWidth() - 2 * marginSize) / image.getWidth(),
                    (originalSize.getHeight() - 2 * marginSize) / image.getHeight()
                );
    
                // Calculate positions to center the scaled content with the margin
                float xPosition = (originalSize.getWidth() - image.getWidth() * scale) / 2;
                float yPosition = (originalSize.getHeight() - image.getHeight() * scale) / 2;
    
                try (PDPageContentStream contentStream = new PDPageContentStream(newDocument, newPage)) {
                    // Draw the image scaled down and centered within the original page size
                    contentStream.drawImage(pdImage, xPosition, yPosition, image.getWidth() * scale, image.getHeight() * scale);
                }
            }
    
            // Save the modified PDF to ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newDocument.save(baos);
            newDocument.close();
            return baos.toByteArray();
        }
    }

    public byte[] addMarginWithContent(
        float marginSize, String position, String alignment, String text, boolean placeText,
        boolean placeImage
    ) throws IOException {
        // First, add margin to the PDF using the previous method
        byte[] pdfWithMargin = addMarginToPdf(marginSize);

        // Load the modified document to add the content in the margin
        try (PDDocument document = PDDocument.load(pdfWithMargin)) {
            for (PDPage page : document.getPages()) {
                PDRectangle pageSize = page.getMediaBox();
                float contentX = marginSize;
                float contentY = marginSize;
                float fontSize = 12;
                PDFont font = PDType1Font.HELVETICA_BOLD;

                // Calculate the height of the text
                float textHeight = estimateTextWidth(font, fontSize, text);

                // Adjust content position based on specified position and alignment
                if (position.equalsIgnoreCase("top") || position.equalsIgnoreCase("bottom")) {
                    // Vertical margins
                    contentY = position.equalsIgnoreCase("top") 
                        ? pageSize.getHeight() - marginSize - textHeight 
                        : marginSize;

                    // Adjust horizontal alignment within the top or bottom margin
                    if (alignment.equalsIgnoreCase("center")) {
                        contentX = (pageSize.getWidth() - 100) / 2;
                    } else if (alignment.equalsIgnoreCase("right")) {
                        contentX = pageSize.getWidth() - marginSize - 100;
                    }
                } else if (position.equalsIgnoreCase("left") || position.equalsIgnoreCase("right")) {
                    // Horizontal margins
                    contentX = position.equalsIgnoreCase("left") 
                        ? marginSize + 20  // Add padding to prevent text cut-off
                        : pageSize.getWidth() - marginSize - 25;

                    // Adjust vertical alignment within the left or right margin
                    if (alignment.equalsIgnoreCase("center")) {
                        contentY = (pageSize.getHeight() - textHeight) / 2;
                    } else if (alignment.equalsIgnoreCase("top")) {
                        contentY = position.equalsIgnoreCase("left") 
                            ? pageSize.getHeight() - marginSize - textHeight - 10 
                            : pageSize.getHeight() - marginSize - 10;  // Adjusted for top alignment
                    } else if (alignment.equalsIgnoreCase("bottom")) {
                        contentY = 25 + textHeight;
                    }
                }

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                    float scaledWidth;
                    float scaledHeight;

                    // Place image if requested, with scaling to fit the margin
                    if (placeImage) {
                        String imagePath = "src/main/resources/images/tech.png";
                        PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);

                        // Calculate available width and height for the margin
                        float availableWidth = pageSize.getWidth() - 2 * marginSize;
                        float availableHeight = pageSize.getHeight() - 2 * marginSize;

                        // Calculate the aspect ratio and scale the image to fit the margin
                        float imageWidth = pdImage.getWidth();
                        float imageHeight = pdImage.getHeight();
                        float scale = Math.min(availableWidth / imageWidth, availableHeight / imageHeight);
                        scale=scale/18;

                        // Calculate scaled image dimensions
                        scaledWidth = imageWidth * scale;
                        scaledHeight = imageHeight * scale;

                        // Draw the scaled image
                        contentStream.drawImage(pdImage, contentX-15, contentY - scaledHeight-5, scaledWidth, scaledHeight);
                    }
                    
                    // Place text if requested
                    if (placeText) {
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);

                        // Apply rotation for left or right margin
                        if (position.equalsIgnoreCase("left")) {
                            // Rotate text 90 degrees clockwise
                            contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2,contentX, contentY));
                        } else if (position.equalsIgnoreCase("right")) {
                            // Rotate text 90 degrees counterclockwise
                            contentStream.setTextMatrix(Matrix.getRotateInstance(-Math.PI / 2, contentX, contentY));
                        } else {
                            // No rotation for top or bottom
                            contentStream.setTextMatrix(Matrix.getTranslateInstance(contentX, contentY));
                        }
                        
                        contentStream.showText(text);
                        contentStream.endText();
                    }

                    
                }
            }

            // Save the final PDF with margin and content to a ByteArrayOutputStream to return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }


        // Helper method to estimate the width of the text based on the font and font size
        private float estimateTextWidth(PDFont font, float fontSize, String text) throws IOException {
            // Calculate the width of the text in points
            return font.getStringWidth(text) / 1000 * fontSize;
        }

         @SuppressWarnings("deprecation")
    public byte[] generateCKYCPdf() throws IOException {
        
        String sectionTitle = "CENTRAL KYC REGISTRY | Know Your Customer (KYC) Application Form | Individual";
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
                // Set font and font size for the title
                float fontSize = 12;
                PDFont font = PDType1Font.TIMES_BOLD;
                contentStream.setFont(font, fontSize);

                // Calculate coordinates for the section title
                float margin = 30;
                float xOffset = margin;
                float yOffset = page.getMediaBox().getHeight() - margin - fontSize;

                // Set background color for the title section
                PDColor backgroundColor = new PDColor(new float[]{15/255f, 180/255f, 240/255f}, PDDeviceRGB.INSTANCE);
                contentStream.setNonStrokingColor(backgroundColor);

                // Calculate the width of the section title using font metrics
                float textWidth = font.getStringWidth(sectionTitle) / 1000 * fontSize;
                float textHeight = fontSize;
                float rectWidth = textWidth + 10; // Add padding
                float rectHeight = textHeight + 5; // Add padding

                // Draw the background rectangle
                contentStream.fillRect(xOffset - 5, yOffset - 5, rectWidth, rectHeight);

                // Draw the section title text
                contentStream.setNonStrokingColor(new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE)); // Set text color to white
                contentStream.beginText();
                contentStream.newLineAtOffset(xOffset, yOffset);
                contentStream.showText(sectionTitle);
                contentStream.endText();

                // Reset color for further content
                contentStream.setNonStrokingColor(new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE)); // Set text color to black for further content

                // Instructions content
                String[] leftColumnTexts = {
                    "A) Fields marked with ‘*’ are mandatory fields.",
                    "B) Please fill the form in English and in BLOCK letters.",
                    "C) Please fill the date in DD-MM-YYYY format.",
                    "D) Please read section wise detailed guidelines / instructions at the end."
                };

                String[] rightColumnTexts = {
                    "E) List of State / U.T code as per Indian Motor Vehicle Act, 1988 is available at the end.",
                    "F) List of two character ISO 3166 country codes is available at the end.",
                    "G) KYC number of applicant is mandatory for update application.",
                    "H) For particular section update, please tick () in the box available before the section number and strike off the sections not required to be updated."
                };

                // Set font for instructions
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);

                // Coordinates for instructions
                float leftColumnX = xOffset;
                float rightColumnX = page.getMediaBox().getWidth() / 2 + margin / 2;
                float instructionsYOffset = yOffset - 40; // Offset for instructions section
                float leading = 15; // Space between lines

                float columnWidth = (page.getMediaBox().getWidth() - (2 * margin)) / 2 - margin; // Width of each column

                // Draw left column instructions with text wrapping
                for (String text : leftColumnTexts) {
                    List<String> lines = wrapText(text, font, 10, columnWidth);
                    for (String line : lines) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(leftColumnX, instructionsYOffset);
                        contentStream.showText(line);
                        contentStream.endText();
                        instructionsYOffset -= leading;
                    }
                }

                // Reset yOffset for the right column
                instructionsYOffset = yOffset - 40;

                // Draw right column instructions with text wrapping
                for (String text : rightColumnTexts) {
                    List<String> lines = wrapText(text, font, 10, columnWidth);
                    for (String line : lines) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(rightColumnX, instructionsYOffset);
                        contentStream.showText(line);
                        contentStream.endText();
                        instructionsYOffset -= leading;
                    }
                }

                // Set font and font size
                float fontSize_sec = 12;
                PDFont font_sec = PDType1Font.TIMES_BOLD;
                contentStream.setFont(font_sec, fontSize_sec);

                // Calculate coordinates for the section title
                float margin_sec = 25;
                float xOffset_sec = margin_sec;
                float yOffset_sec = page.getMediaBox().getHeight() - margin - fontSize-100;

                // Set background color for the title section
                PDColor backgroundColor_sec = new PDColor(new float[]{15/255f, 180/255f, 240/255f}, PDDeviceRGB.INSTANCE);
                contentStream.setNonStrokingColor(backgroundColor_sec);


                // Draw the background rectangle
                contentStream.fillRect(xOffset - 5, yOffset - 5, rectWidth, rectHeight);

                // Draw the section title text
                contentStream.setNonStrokingColor(new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE)); // Set text color to white
                contentStream.beginText();
                contentStream.newLineAtOffset(xOffset, yOffset);
                contentStream.showText(sectionTitle);
                contentStream.endText();

                // Reset color for further content
                contentStream.setNonStrokingColor(new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE));

                // Draw the "For office use only" section using the helper function
                drawOfficeUseOnlySection(contentStream, xOffset_sec, yOffset_sec - 50);
                drawtitleSection1(contentStream, xOffset, yOffset - 230); // Adjust offset as needed
                drawPersonalDetailsSection(contentStream, xOffset_sec+25, yOffset_sec-170);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Helper method to wrap text into lines that fit within the specified width.
     */
    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + (line.length() > 0 ? " " : "") + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;
            if (textWidth > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line.append((line.length() > 0 ? " " : "")).append(word);
            }
        }
        lines.add(line.toString()); // Add the last line
        return lines;
    }

    @SuppressWarnings("deprecation")
    public static void drawOfficeUseOnlySection(PDPageContentStream contentStream, float xOffset, float yOffset) throws IOException {
        // Set background color for the entire section
        PDColor backgroundColor = new PDColor(new float[]{220/255f, 220/255f, 220/255f}, PDDeviceRGB.INSTANCE);
        contentStream.setNonStrokingColor(backgroundColor);
        // Draw background rectangle behind the entire section
        float sectionHeight = 70;
        float sectionWidth = 520; // Adjust width as necessary
        contentStream.addRect(xOffset, yOffset - sectionHeight, sectionWidth, sectionHeight);
        contentStream.fill();
        // Reset color for text drawing
        contentStream.setNonStrokingColor(0); // Set text color to black
        // Set font and font size for smaller scale
        float fontSize = 8;
        contentStream.setFont(PDType1Font.TIMES_BOLD, fontSize);
        // Draw "For office use only" label
        contentStream.beginText();
        contentStream.newLineAtOffset(xOffset + 5, yOffset - 10);
        contentStream.showText("For office use only");
        contentStream.endText();
        // Draw "(To be filled by financial institution)" in italics under the label
        contentStream.setFont(PDType1Font.TIMES_ITALIC, fontSize - 1);
        contentStream.beginText();
        contentStream.newLineAtOffset(xOffset + 5, yOffset - 20);
        contentStream.showText("(To be filled by financial institution)");
        contentStream.endText();
        // Reset font to bold for other labels
        contentStream.setFont(PDType1Font.TIMES_BOLD, fontSize);
        // Draw "Application Type*" label and options on the same line
        float applicationTypeX = xOffset + 150;
        float applicationTypeYOffset = yOffset - 10;
        contentStream.beginText();
        contentStream.newLineAtOffset(applicationTypeX, applicationTypeYOffset);
        contentStream.showText("Application Type*");
        contentStream.endText();
        // Draw checkboxes for "New" and "Update"
        float checkboxX = applicationTypeX + 80;
        contentStream.beginText();
        contentStream.newLineAtOffset(checkboxX, applicationTypeYOffset);
        contentStream.showText("New");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(checkboxX + 40, applicationTypeYOffset);
        contentStream.showText("Update");
        contentStream.endText();


        // Move to next line for "KYC Number"
        float kycNumberYOffset = applicationTypeYOffset - 15;
        contentStream.beginText();
        contentStream.showText("KYC Number");
        contentStream.endText();
        // Draw boxes for KYC Number
        float boxSize = 12; // Increased box size
        float boxPadding = 4; // Increased padding between boxes
        float boxX = applicationTypeX + 80;
        for (int i = 0; i < 14; i++) {
            contentStream.addRect(boxX + (i * (boxSize + boxPadding)), kycNumberYOffset - 5, boxSize, boxSize);
        }
        contentStream.stroke();
        // Move to the next line for "(Mandatory for KYC update request)"
        float mandatoryTextYOffset = kycNumberYOffset - 20; // Adjusted Y offset for the next line
        contentStream.beginText();
        contentStream.newLineAtOffset(boxX + 150, mandatoryTextYOffset); // Use the new Y offset
        contentStream.showText("(Mandatory for KYC update request)");
        contentStream.endText();
        // Move to the next line for "Account Type*"
        float accountTypeYOffset = mandatoryTextYOffset - 15; // Move down for Account Type label
        // Draw "Account Type*" label and options
        contentStream.beginText();
        contentStream.newLineAtOffset(applicationTypeX, accountTypeYOffset);
        contentStream.showText("Account Type*");
        contentStream.endText();
        // Draw checkboxes for "Normal", "Simplified (for low risk customers)", and "Small"
        contentStream.beginText();
        contentStream.newLineAtOffset(checkboxX, accountTypeYOffset);
        contentStream.showText("Normal");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(checkboxX + 60, accountTypeYOffset);
        contentStream.showText("Simplified (for low risk customers)");
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(checkboxX + 180, accountTypeYOffset);
        contentStream.showText("Small");
        contentStream.endText();
    
        // Call the next section method with the updated yOffset
    }
    
    @SuppressWarnings("deprecation")
    public static void drawtitleSection1(PDPageContentStream contentStream, float xOffset, float yOffset) throws IOException {
        // Set background color for the entire section
        PDColor backgroundColor = new PDColor(new float[]{15/255f, 180/255f, 240/255f}, PDDeviceRGB.INSTANCE);
        contentStream.setNonStrokingColor(backgroundColor);
        // Draw background rectangle behind the entire section
        float sectionHeight = 20; // Adjust height as needed
        float sectionWidth = 520; // Adjust width as necessary
        contentStream.addRect(xOffset, yOffset - sectionHeight, sectionWidth, sectionHeight);
        contentStream.fill();
        // Reset color for text drawing
        contentStream.setNonStrokingColor(255); // Set text color to black
        // Set font and font size for smaller scale
        float fontSize = 8;
        contentStream.setFont(PDType1Font.TIMES_BOLD, fontSize);
        // Draw "1. Personal Details" label
        contentStream.beginText();
        contentStream.newLineAtOffset(xOffset + 5, yOffset - 15);
        contentStream.showText("1. Personal Details");
        contentStream.endText();
    }

    @SuppressWarnings("deprecation")
    public static void drawPersonalDetailsSection(PDPageContentStream contentStream, float startX, float startY) throws IOException {
        // Set font and font size for text labels
        PDFont font = PDType1Font.TIMES_ROMAN;
        float fontSize = 8;
        contentStream.setFont(font, fontSize);

        // Set text color to black
        contentStream.setNonStrokingColor(0); 

        // Initial y-offset for rows
        float y = startY;

        // Increase the leading value to add more space between lines
        float leading = 20; // Adjust the value as needed for more or less spacing

        // Row 1: "Name (Same as ID proof)" checkbox and fields for Prefix, First Name, Middle Name, Last Name
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Name* (Same as ID proof)");
        contentStream.endText();
        drawCheckbox(contentStream, startX - 15, y - 3);
        drawTextField(contentStream, startX + 100, y - 10, 30); // Prefix
        drawTextField(contentStream, startX + 140, y - 10, 100); // First Name
        drawTextField(contentStream, startX + 250, y - 10, 100); // Middle Name
        drawTextField(contentStream, startX + 360, y - 10, 100); // Last Name

        y -= leading;

        // Row 2: Maiden Name
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Maiden Name (If any*)");
        contentStream.endText();
        drawTextField(contentStream, startX + 140, y - 10, 320); // Maiden Name field

        y -= leading;

        // Row 3: Father / Spouse Name
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Father / Spouse Name*");
        contentStream.endText();
        drawTextField(contentStream, startX + 140, y - 10, 320); // Father/Spouse Name field

        y -= leading;

        // Row 4: Mother Name
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Mother Name*");
        contentStream.endText();
        drawTextField(contentStream, startX + 140, y - 10, 320); // Mother Name field

        y -= leading;

        // Row 5: Date of Birth
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Date of Birth*");
        contentStream.endText();
        drawTextField(contentStream, startX + 140, y - 10, 80); // Date of Birth field (DD-MM-YYYY format)

        y -= leading;

        // Row 6: Gender checkboxes (Male, Female, Transgender)
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Gender*");
        contentStream.endText();
        drawCheckboxWithLabel(contentStream, startX + 140, y, "M- Male");
        drawCheckboxWithLabel(contentStream, startX + 220, y, "F- Female");
        drawCheckboxWithLabel(contentStream, startX + 300, y, "T- Transgender");

        y -= leading;

        // Row 7: Marital Status checkboxes (Married, Unmarried, Others)
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Marital Status*");
        contentStream.endText();
        drawCheckboxWithLabel(contentStream, startX + 140, y, "Married");
        drawCheckboxWithLabel(contentStream, startX + 220, y, "Unmarried");
        drawCheckboxWithLabel(contentStream, startX + 300, y, "Others");

        y -= leading;

        // Row 7: Citizenship checkboxes (IN- Indian, Others with ISO 3166 Country Code)
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.showText("Citizenship*");
        contentStream.endText();
        drawCheckboxWithLabel(contentStream, startX + 140, y, "IN- Indian");
        drawCheckboxWithLabel(contentStream, startX + 220, y, "Others (ISO 3166 Country Code");
        
        drawTextField(contentStream, startX + 365, y - 3, 10); // First box for Country Code
        drawTextField(contentStream, startX + 380, y - 3, 10); // Second box for Country Code

        //Row 8 : residential address
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y-25);
        contentStream.showText("Residential Status*");
        contentStream.endText();
        drawCheckboxWithLabel(contentStream, startX + 140, y-25, "Resident Individual");
        drawCheckboxWithLabel(contentStream, startX + 230, y-25, "Non Resident Indian");
        drawCheckboxWithLabel(contentStream, startX + 140, y-45, "Foreign National");
        drawCheckboxWithLabel(contentStream, startX + 230, y-45, "Person of Indian Origin");

        //Row 9 : Occupation Type 
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y-65);
        contentStream.showText("Occupation Type*");
        contentStream.endText();
        drawCheckboxWithLabel(contentStream, startX + 140, y-65, "S-Service");
        drawCheckboxWithLabel(contentStream, startX + 140, y-80, "O-Others");
        drawCheckboxWithLabel(contentStream, startX + 140, y-94, "B-Business");
        drawCheckboxWithLabel(contentStream, startX + 140, y-109, "X- Not Categorisedn");
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y-65);
        contentStream.showText("(");
        contentStream.endText();
        



        // Adjust y-coordinate for further content if needed
    }

    // Helper method to draw a checkbox at specified coordinates
    public static void drawCheckbox(PDPageContentStream contentStream, float x, float y) throws IOException {
        contentStream.addRect(x, y, 10, 10);
        contentStream.stroke();
    }

    // Helper method to draw a checkbox with a label
    public static void drawCheckboxWithLabel(PDPageContentStream contentStream, float x, float y, String label) throws IOException {
        drawCheckbox(contentStream, x, y - 3); // Draw checkbox
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 15, y);
        contentStream.showText(label);
        contentStream.endText();
    }

    // Helper method to draw a text input field (rectangle)
    public static void drawTextField(PDPageContentStream contentStream, float x, float y, float width) throws IOException {
        contentStream.addRect(x, y, width, 15);
        contentStream.stroke();
    }

    


    

}    


