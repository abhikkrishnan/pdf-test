package com.pdf.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
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
            Font watermarkFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 50, Font.ITALIC, new Color(200, 200, 200));
            Phrase watermark = new Phrase("Test document", watermarkFont);

            // Set opacity
            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.3f); 

            // Set diagonal position
            float x = (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / 2;
            float y = (document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin()) / 2;

            // Rotate to make it diagonal
            canvas.saveState();
            canvas.setGState(gState);
            canvas.beginText();
            canvas.setFontAndSize(watermarkFont.getBaseFont(), 50);
            canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, watermark.getContent(), x, y, 45);
            canvas.endText();
            canvas.restoreState();
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Phrase header = new Phrase("TechArchitect - Header", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            Phrase footer = new Phrase("TechArchitects", FontFactory.getFont(FontFactory.HELVETICA, 12));
            
            // Add header
            canvas.beginText();
            canvas.setFontAndSize(header.getFont().getBaseFont(), 12);
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "TechArchitect - Header", document.left(), document.top() + 10, 0);
            canvas.endText();
            
            // Add footer
            canvas.beginText();
            canvas.setFontAndSize(footer.getFont().getBaseFont(), 12);
            canvas.showTextAligned(PdfContentByte.ALIGN_RIGHT, "TechArchitects", document.right(), document.bottom() - 10, 0);
            canvas.endText();
        }
    }
}
