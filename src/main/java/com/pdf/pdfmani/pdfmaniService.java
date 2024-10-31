package com.pdf.pdfmani;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
}


