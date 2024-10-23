package com.pdf.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;



@Service
public class PdfService {
    private Logger logger = LoggerFactory.getLogger(PdfService.class);

    public ByteArrayInputStream createPdf() {
        logger.info("Create PDF Started : ");
        String title = "TechArchitect by Paras Bagga";
        String content = "We provide Web Development Services";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 35, 35, 25, 25);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            Watermark event = new Watermark();
            writer.setPageEvent(event);

            document.open();

            // Adding first page content
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25);
            Paragraph titlePara = new Paragraph(title, titleFont);
            document.add(titlePara);

            Font paraFont = FontFactory.getFont(FontFactory.HELVETICA, 18);
            Paragraph paragraph = new Paragraph(content, paraFont);
            paragraph.add(" This text is added while creating PDF.");
            document.add(paragraph);

            // Add new page before importing content from existing PDF
            document.newPage();

            // Importing an existing PDF page
            File file = new File("src/main/resources/pdf/existing.pdf"); // Update the path accordingly
            PdfReader reader = new PdfReader(new FileInputStream(file));
            PdfContentByte contentByte = writer.getDirectContent();
            PdfImportedPage page = writer.getImportedPage(reader, 1); 
            contentByte.addTemplate(page, 0, 0);

            reader.close();
        } catch (DocumentException | IOException ex) {
            logger.error("Error occurred: {}", ex.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    class Watermark extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
    
            // Dynamically calculate the font size based on page size
            float fontSize = calculateFontSize(document.getPageSize());
    
            // Define the font for the watermark with dynamic size
            Font watermarkFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize, Font.ITALIC, new Color(200, 200, 200));
            Phrase watermark = new Phrase("Test document", watermarkFont);
    
            // Set opacity for the watermark
            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.3f);
    
            // Calculate center position for watermark
            float x = (document.right() + document.left()) / 2;
            float y = (document.top() + document.bottom()) / 2;
    
            // Rotate the watermark diagonally
            canvas.saveState();
            canvas.setGState(gState);
            canvas.beginText();
            canvas.setFontAndSize(watermarkFont.getBaseFont(), fontSize);
            canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, watermark.getContent(), x, y, 45);
            canvas.endText();
            canvas.restoreState();
        }
    
        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
    
            // Dynamically calculate the font size based on page size
            float fontSize = calculateFontSize(document.getPageSize());
    
            // Define the fonts for the header and footer with dynamic size
            Phrase header = new Phrase("TechArchitect - Header", FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
            Phrase footer = new Phrase("TechArchitects", FontFactory.getFont(FontFactory.HELVETICA, fontSize));
    
            // Calculate the top and bottom positions dynamically based on the page size
            float headerY = document.top() - (fontSize+1); // A bit above the top margin
            float footerY = document.bottom(); // A bit below the bottom margin
    
            // Add header aligned to the left
            canvas.beginText();
            canvas.setFontAndSize(header.getFont().getBaseFont(), fontSize);
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, header.getContent(), document.left(), headerY, 0);
            canvas.endText();
    
            // Add footer aligned to the right
            canvas.beginText();
            canvas.setFontAndSize(footer.getFont().getBaseFont(), fontSize);
            canvas.showTextAligned(PdfContentByte.ALIGN_RIGHT, footer.getContent(), document.right(), footerY, 0);
            canvas.endText();
        }
    
        // Helper method to calculate font size based on the page size
        private float calculateFontSize(Rectangle pageSize) {
            // Get diagonal size of the page (Pythagorean theorem for width and height)
            float diagonal = (float) Math.sqrt(Math.pow(pageSize.getWidth(), 2) + Math.pow(pageSize.getHeight(), 2));
    
            // Map the diagonal length to a font size between 12 and 26
            // Assuming A6 to A1 ranges approximately from 300 to 2400 diagonal size
            float minDiagonal = 300;  // Approximate diagonal for A6
            float maxDiagonal = 2400; // Approximate diagonal for A1
    
            // Scale the font size linearly between 12 and 26 based on diagonal
            float minFontSize = 9;
            float maxFontSize = 32;
    
            // Clamp the diagonal size between minDiagonal and maxDiagonal
            diagonal = Math.max(minDiagonal, Math.min(maxDiagonal, diagonal));
    
            // Linear interpolation to calculate the font size
            return minFontSize + (diagonal - minDiagonal) * (maxFontSize - minFontSize) / (maxDiagonal - minDiagonal);
        }
    }
    
    

    public ByteArrayInputStream manipulate_pdf(Rectangle targetPageSize) {
        logger.info("Create PDF Started ");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    
        // Dynamically calculate the margins based on page size
        float[] margins = calculateMargins(targetPageSize);
        float marginLeft = margins[0];
        float marginRight = margins[1];
        float marginTop = margins[2];
        float marginBottom = margins[3];
    
        // Create the document with dynamically calculated margins
        Document document = new Document(targetPageSize, marginLeft, marginRight, marginTop, marginBottom);
        Rectangle pageSize = null;
    
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            Watermark event = new Watermark();
            writer.setPageEvent(event);
    
            document.open();
    
            // Importing an existing PDF page
            File file = new File("src/main/resources/pdf/existing.pdf");
            PdfReader reader = new PdfReader(new FileInputStream(file));
            pageSize = reader.getPageSize(1); // Original size of the imported PDF page
    
            PdfContentByte contentByte = writer.getDirectContent();
            PdfImportedPage page = writer.getImportedPage(reader, 1);
    
            // Calculate scaling factors to fit the imported page into the target size (considering margins)
            float availableWidth = targetPageSize.getWidth() - marginLeft - marginRight;
            float availableHeight = targetPageSize.getHeight() - marginTop - marginBottom;
    
            float scaleX = availableWidth / pageSize.getWidth();
            float scaleY = availableHeight / pageSize.getHeight();
            float scale = Math.min(scaleX, scaleY); // Maintain aspect ratio by using the smaller scaling factor
    
            // Calculate the offset to center the content on the new page, considering margins
            float offsetX = marginLeft + (availableWidth - (pageSize.getWidth() * scale)) / 2;
            float offsetY = marginBottom + (availableHeight - (pageSize.getHeight() * scale)) / 2;
    
            // Add the template, scaling and centering it on the new page size with margins
            contentByte.addTemplate(page, scale, 0, 0, scale, offsetX, offsetY);
    
            reader.close();
        } catch (DocumentException | IOException ex) {
            logger.error("Error occurred: {}", ex.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    
        if (pageSize != null) {
            logger.info("Page size of the imported PDF: " + pageSize);
        }
    
        return new ByteArrayInputStream(out.toByteArray());
    }
    
    // Helper method to calculate dynamic margins based on the page size
    private float[] calculateMargins(Rectangle pageSize) {
        // Get diagonal size of the page (Pythagorean theorem for width and height)
        float diagonal = (float) Math.sqrt(Math.pow(pageSize.getWidth(), 2) + Math.pow(pageSize.getHeight(), 2));
    
        // Map diagonal to margin sizes
        float minDiagonal = 500;  // Approximate diagonal for A6
        float maxDiagonal = 4400; // Approximate diagonal for A1
    
        // Minimum and maximum margins
        float minMargin = 10; // Small margin for smaller pages (like A6)
        float maxMargin = 60; // Larger margin for larger pages (like A1)
    
        // Clamp the diagonal size between minDiagonal and maxDiagonal
        diagonal = Math.max(minDiagonal, Math.min(maxDiagonal, diagonal));
    
        // Calculate margins proportionally (linear interpolation between minMargin and maxMargin)
        float margin = minMargin + (diagonal - minDiagonal) * (maxMargin - minMargin) / (maxDiagonal - minDiagonal);
    
        // Return equal margins for left, right, top, bottom
        return new float[] { margin, margin, margin, margin };
    }
    
    
}
