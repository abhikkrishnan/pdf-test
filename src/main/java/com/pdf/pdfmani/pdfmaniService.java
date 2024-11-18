package com.pdf.pdfmani;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.logging.log4j.util.Strings;
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
                    List<String> lines = wrapTextToWidth(paragraph, PDType1Font.HELVETICA, FONT_SIZE,
                            page.getMediaBox().getWidth() - 2 * MARGIN);

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
    private List<String> wrapTextToWidth(String text, PDType1Font font, int fontSize, float maxWidth)
            throws IOException {
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
        marginSize *= 72;
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
                        (originalSize.getHeight() - 2 * marginSize) / image.getHeight());

                // Calculate positions to center the scaled content with the margin
                float xPosition = (originalSize.getWidth() - image.getWidth() * scale) / 2;
                float yPosition = (originalSize.getHeight() - image.getHeight() * scale) / 2;

                try (PDPageContentStream contentStream = new PDPageContentStream(newDocument, newPage)) {
                    // Draw the image scaled down and centered within the original page size
                    contentStream.drawImage(pdImage, xPosition, yPosition, image.getWidth() * scale,
                            image.getHeight() * scale);
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
            boolean placeImage) throws IOException {
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
                            ? marginSize + 20 // Add padding to prevent text cut-off
                            : pageSize.getWidth() - marginSize - 25;

                    // Adjust vertical alignment within the left or right margin
                    if (alignment.equalsIgnoreCase("center")) {
                        contentY = (pageSize.getHeight() - textHeight) / 2;
                    } else if (alignment.equalsIgnoreCase("top")) {
                        contentY = position.equalsIgnoreCase("left")
                                ? pageSize.getHeight() - marginSize - textHeight - 10
                                : pageSize.getHeight() - marginSize - 10; // Adjusted for top alignment
                    } else if (alignment.equalsIgnoreCase("bottom")) {
                        contentY = 25 + textHeight;
                    }
                }

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                        PDPageContentStream.AppendMode.APPEND, true)) {

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
                        scale = scale / 18;

                        // Calculate scaled image dimensions
                        scaledWidth = imageWidth * scale;
                        scaledHeight = imageHeight * scale;

                        // Draw the scaled image
                        contentStream.drawImage(pdImage, contentX - 15, contentY - scaledHeight - 5, scaledWidth,
                                scaledHeight);
                    }

                    // Place text if requested
                    if (placeText) {
                        contentStream.beginText();
                        contentStream.setFont(font, fontSize);

                        // Apply rotation for left or right margin
                        if (position.equalsIgnoreCase("left")) {
                            // Rotate text 90 degrees clockwise
                            contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, contentX, contentY));
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

            // Save the final PDF with margin and content to a ByteArrayOutputStream to
            // return as byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    // Helper method to estimate the width of the text based on the font and font
    // size
    private float estimateTextWidth(PDFont font, float fontSize, String text) throws IOException {
        // Calculate the width of the text in points
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    // private  byte[] placeImgae(PDDocument document,PDPage page,float x,float y){
    //     String imagePath = "src/main/resources/images/tech.png";
    //     PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);
    //     PDRectangle pageSize = page.getMediaBox();

    //     // Calculate available width and height for the margin
    //     float availableWidth = pageSize.getWidth() ;
    //     float availableHeight = pageSize.getHeight();

    //     // Calculate the aspect ratio and scale the image to fit the margin
    //     float imageWidth = pdImage.getWidth();
    //     float imageHeight = pdImage.getHeight();
    //     float scale = Math.min(availableWidth / imageWidth, availableHeight / imageHeight);
    //     scale = scale / 18;

    //     // Calculate scaled image dimensions
    //     float scaledWidth = imageWidth * scale;
    //     float scaledHeight = imageHeight * scale;

    //     // Draw the scaled image
    //     contentStream.drawImage(pdImage, x, y, scaledWidth,
    //             scaledHeight);
    // }

    public String add_text(Object[] texts){

        System.out.println("\n Received texts service: " + Arrays.toString(texts));

        List<Map<String, Object>>texts_array=new ArrayList<>();
       
        for (Object obj : texts) {
            System.out.println(obj);

            if (obj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                texts_array.add(map);
            }
        }
    
        // Now textsArray is a List<Map<String, Object>>
        System.out.println("\n Converted texts array: " + texts_array);

        return "Hi";

    }
    


}
