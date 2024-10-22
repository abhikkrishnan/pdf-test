package com.pdf.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfService {
    private Logger logger = LoggerFactory.getLogger(PdfService.class);

    public ByteArrayInputStream createPdf() {
        logger.info("Create PDF Started : ");
        String title = "TechArchitect by Paras Bagga";
        String content = "We provide Web Development Services";
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Setting margins: 25px left, right, top, bottom
        Document document = new Document(PageSize.A4, 35, 35, 25, 25);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            Watermark event = new Watermark();
            writer.setPageEvent(event);

            // Adding header
            HeaderFooter header = new HeaderFooter(new Phrase("TechArchitect - Header"), false);
            header.setBorder(Rectangle.BOTTOM); // Set border at the bottom
            document.setHeader(header);

            // Adding footer
            HeaderFooter footer = new HeaderFooter(new Phrase("TechArchitects"), false);
            footer.setBorderWidthBottom(0);
            document.setFooter(footer);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25);
            Paragraph titlePara = new Paragraph(title, titleFont);
            document.add(titlePara);

            Font paraFont = FontFactory.getFont(FontFactory.HELVETICA, 18);
            Paragraph paragraph = new Paragraph(content, paraFont);
            paragraph.add(" This text is added while creating PDF.");
            document.add(paragraph);
            

        } catch (DocumentException ex) {
            logger.error("Error occurred: {0}", ex);
        } finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    class Watermark extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Font watermarkFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 50, Font.ITALIC, new Color(200, 200, 200,10));
            Phrase watermark = new Phrase("Test document", watermarkFont);

            // Set opacity
            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.3f); // Set opacity to 30%

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
    }
}
