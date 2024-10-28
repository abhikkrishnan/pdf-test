package com.pdf.services;

import java.lang.Float;
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

// import com.itextpdf.awt.geom.CubicCurve2D.Float;
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

    public ByteArrayInputStream createPdf(Boolean merge) {
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


            String para_content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium, purus ut interdum interdum, magna augue dignissim elit, vitae tempus ipsum metus porta nisl. Proin vitae odio ac felis mollis vestibulum vitae at nisi. Donec a condimentum lectus. Pellentesque neque libero, molestie eget nulla ut, pharetra accumsan magna. Lorem ipsum dolor sit amet, consectetur adipiscing elit. In consequat efficitur purus, sed fringilla ligula pretium vitae. Cras molestie pharetra nisl ut imperdiet. Vivamus feugiat, felis ut ornare ullamcorper, nulla augue ornare nunc, quis cursus risus dui a tellus. Fusce efficitur tincidunt purus, at varius dui semper bibendum. Vestibulum eleifend ligula vitae finibus dignissim. Nunc leo enim, imperdiet non imperdiet at, suscipit accumsan mi. Donec quis consectetur odio. Suspendisse rutrum leo id dui fermentum elementum. Quisque sit amet scelerisque magna. Etiam sed tortor nec neque pulvinar consequat.\r\n" + //
                                "\r\n" + //
                                "Sed ullamcorper, elit vitae commodo finibus, leo orci facilisis magna, vitae pulvinar augue leo nec mi. Sed vulputate sed ipsum ac rhoncus. Curabitur aliquet lacus ante, ut bibendum ipsum convallis sed. Vestibulum vestibulum ornare tempor. Aenean at luctus libero. Vivamus sed diam purus. Aliquam iaculis ligula non semper efficitur. Donec blandit, nisl vitae tincidunt feugiat, nulla orci semper ligula, vel tincidunt ipsum risus non nisi. Morbi lacinia ipsum eu lectus volutpat scelerisque. Curabitur viverra tincidunt sem, at gravida nisl lobortis quis. Nam consequat mi sed dui aliquet scelerisque. Nam id velit ultrices, feugiat risus sed, pharetra diam. Curabitur arcu justo, aliquam eu urna a, gravida hendrerit nisl. Etiam scelerisque mauris id mollis accumsan. Praesent faucibus porttitor tempor. Vivamus molestie aliquet enim.\r\n" + //
                                "\r\n" + //
                                "Aliquam erat volutpat. Curabitur congue molestie posuere. Curabitur quis libero pretium, euismod libero ac, posuere orci. Suspendisse potenti. Vivamus ac turpis commodo, ornare odio a, semper nibh. Sed nisl augue, vulputate eu metus in, ullamcorper hendrerit ipsum. Suspendisse potenti. Aliquam et dapibus nisi. Nulla aliquam non lectus id vulputate. Curabitur efficitur lacus justo, id semper libero venenatis ac. Nullam faucibus eget nunc at pharetra. Suspendisse eros ex, convallis id velit nec, consectetur varius nunc. Nunc vel nunc lectus. Suspendisse massa dolor, fermentum vitae erat a, placerat sodales lectus. Nullam rutrum nunc eget risus volutpat fermentum.\r\n" + //
                                "\r\n" + //
                                "Vestibulum eu felis diam. Nam sed condimentum mi, et ultricies dui. Quisque eget accumsan lectus, sit amet sollicitudin ipsum. Aenean quis nisi sollicitudin, dapibus neque sit amet, tempor magna. Suspendisse potenti. Fusce lacinia massa justo, quis dignissim leo finibus et. Proin venenatis hendrerit neque, posuere consectetur diam tempus eget. Aliquam feugiat arcu tortor, eget vestibulum sem aliquet quis. Morbi ac justo sollicitudin, pulvinar massa sit amet, rhoncus nisl. Nullam tincidunt urna scelerisque placerat auctor. Donec magna mi, maximus eget tellus nec, venenatis aliquam sem. Proin vestibulum dui ac fermentum consequat. Etiam est massa, porta vel felis non, elementum mollis urna. Nullam lacinia sem in convallis sagittis.\r\n" + //
                                "\r\n" + //
                                "Vivamus in ornare erat. Duis imperdiet justo vitae neque elementum semper. Sed elementum, urna nec tristique aliquam, arcu mauris sollicitudin tellus, quis pharetra ligula est vel nisl. Mauris volutpat tempor ex sit amet egestas. Aenean bibendum, tellus non gravida pretium, mauris purus laoreet.";

            Font paraFont = FontFactory.getFont(FontFactory.HELVETICA, 18);
            Paragraph paragraph = new Paragraph(content, paraFont);
            paragraph.add(para_content);
            document.add(paragraph);



             if (merge) {
                // Add new page before importing content from existing PDF
                document.newPage();

                // Get the manipulated PDF page
                ByteArrayInputStream mergedPdfStream = manipulate_pdf(PageSize.A4,false);
                PdfReader reader = new PdfReader(mergedPdfStream);
                
                PdfContentByte contentByte = writer.getDirectContent();
                PdfImportedPage page = writer.getImportedPage(reader, 1);

                contentByte.addTemplate(page, 0, 0);

                reader.close();
            }

        } catch (DocumentException | IOException  ex) {
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
        public void onStartPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
        
            // Dynamically calculate the font size based on page size
            float fontSize = calculateFontSize(document.getPageSize());
        
            // Define the fonts for the header, footer, and margin text
            Phrase header = new Phrase("TechArchitect - Header", FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize));
            Phrase footer = new Phrase("TechArchitects", FontFactory.getFont(FontFactory.HELVETICA, fontSize));
            Phrase leftMarginText = new Phrase("Left Margin Text", FontFactory.getFont(FontFactory.HELVETICA, fontSize));
            Phrase rightMarginText = new Phrase("Right Margin Text", FontFactory.getFont(FontFactory.HELVETICA, fontSize));
        
            // Calculate the top, bottom, and margin positions dynamically based on the page size
            float headerY = document.top() - (fontSize + 1); // A bit above the top margin
            float footerY = document.bottom(); // A bit below the bottom margin
            float marginLeft = document.left()-fontSize; // Position left text outside the content area
            float marginRight = document.right()+fontSize; // Position right text outside the content area
            float verticalCenter = (document.top() + document.bottom()) / 2; // Vertical center of the page
        
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
        
            // Add left margin text aligned to the center of the page
            canvas.beginText();
            canvas.setFontAndSize(leftMarginText.getFont().getBaseFont(), fontSize);
            canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, leftMarginText.getContent(), marginLeft, verticalCenter, 90); // 90-degree rotation for vertical alignment
            canvas.endText();
        
            // Add right margin text aligned to the center of the page
            canvas.beginText();
            canvas.setFontAndSize(rightMarginText.getFont().getBaseFont(), fontSize);
            canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, rightMarginText.getContent(), marginRight, verticalCenter, -90); // -90-degree rotation for vertical alignment
            canvas.endText();
        }
    
        // Helper method to calculate font size based on the page size
        private float calculateFontSize(Rectangle pageSize) {
            // Get diagonal size of the page (Pythagorean theorem for width and height)
            float diagonal = (float) Math.sqrt(Math.pow(pageSize.getWidth(), 2) + Math.pow(pageSize.getHeight(), 2));
    
            // Map the diagonal length to a font size between 12 and 26
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
    
    
    

    public ByteArrayInputStream manipulate_pdf(Rectangle targetPageSize,Boolean header) {
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

            if (header) {
                Watermark event = new Watermark();
                writer.setPageEvent(event);
                
            }
    
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

    public ByteArrayInputStream add_margin(float left_margin, float right_margin, float top_margin, float bottom_margin) {

        left_margin *=72;
        right_margin*=72;
        top_margin*=72;
        bottom_margin*=72;


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);


    
        PdfWriter writer = PdfWriter.getInstance(document, out);
        float translationX = 0.0f;
        float translationY = 0.0f;
    
        document.open();
    
        // Importing an existing PDF page
        File file = new File("src/main/resources/pdf/existing.pdf");
        PdfReader reader;
        try {
            // Ensure all margins are non-negative
            if (left_margin < 0) left_margin = 0.0f;
            if (right_margin < 0) right_margin = 0.0f;
            if (top_margin < 0) top_margin = 0.0f;
            if (bottom_margin < 0) bottom_margin = 0.0f;
    
            // Define maximum margin for scaling calculation
            Float maxMargin = 200.0f;
    
            // Cap each margin to the maximum threshold
            if (left_margin > maxMargin) left_margin = maxMargin;
            if (right_margin > maxMargin) right_margin = maxMargin;
            if (top_margin > maxMargin) top_margin = maxMargin;
            if (bottom_margin > maxMargin) bottom_margin = maxMargin;
    
            // Calculate horizontal (X-axis) and vertical (Y-axis) scaling factors
            float scalefactor = 1.0f - ((float) (left_margin + right_margin) / (float) maxMargin) * 0.5f;
            float scaleY = 1.0f - ((float) (top_margin + bottom_margin) / (float) maxMargin) * 0.3f;
    
            // Adjust right and bottom margins to add extra space if needed
            if (right_margin != 0) right_margin += 30;
            if (bottom_margin != 0) bottom_margin += 100;
    
            // Calculate translation for horizontal positioning (X-axis)
            float originalWidth = PageSize.A4.getWidth();
            float scaledWidth = originalWidth * scalefactor;
            if (left_margin != 0) {
                translationX = originalWidth - scaledWidth - right_margin;
            }
    
            // Calculate translation for vertical positioning (Y-axis)
            float originalHeight = PageSize.A4.getHeight();
            float scaledHeight = originalHeight * scaleY;
    
            // Set translationY based on independent top and bottom adjustments
            if (bottom_margin!=0) {
                translationY = bottom_margin - (originalHeight - scaledHeight - top_margin);  
            }

            if (top_margin==0 && bottom_margin!=0) {
                translationY=translationY-35;
            }
            reader = new PdfReader(new FileInputStream(file));
    
            PdfContentByte contentByte = writer.getDirectContent();
            PdfImportedPage page = writer.getImportedPage(reader, 1);
    
            // Add template with both horizontal and vertical scaling and translations
            contentByte.addTemplate(page, scalefactor, 0, 0, scaleY, translationX, translationY);
    
            reader.close();
    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    
        return new ByteArrayInputStream(out.toByteArray());
    }

        // New method to add text at specified margin positions
        public ByteArrayInputStream addTextToMargin(String marginPosition, String alignment, String text) {
            logger.info("Adding text to margin: Position - {}, Alignment - {}, Text - {}", marginPosition, alignment, text);

            ByteArrayInputStream pdfWithMargins;




            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            try {
                // pdf with margin 
                if (marginPosition.equalsIgnoreCase("left")) {
                    System.out.println(marginPosition.toLowerCase());
                    pdfWithMargins = add_margin(0.5f, 0f, 0f, 0f);
                } else if (marginPosition.equalsIgnoreCase("right")) {
                    pdfWithMargins = add_margin(0f, 0.5f, 0f, 0f);
                } else if (marginPosition.equalsIgnoreCase("top")) {
                    pdfWithMargins = add_margin(0f, 0f, 0.5f, 0f);
                } else if (marginPosition.equalsIgnoreCase("bottom")) {
                    pdfWithMargins = add_margin(0f, 0f, 0f, 0.5f);
                } else {
                    pdfWithMargins = add_margin(0f, 0f, 0f, 0f);
                }
                

                
                // Then, apply watermark to place text at the specified margin position
                PdfReader reader = new PdfReader(pdfWithMargins);
                Document document = new Document(reader.getPageSize(1));
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();
                
                // Set custom watermark event to add text in margin
                CustomWatermark watermark = new CustomWatermark(marginPosition, alignment, text);
                writer.setPageEvent(watermark);
    
                // Import the existing PDF content
                PdfContentByte contentByte = writer.getDirectContent();
                PdfImportedPage page = writer.getImportedPage(reader, 1);
                contentByte.addTemplate(page, 0, 0);
    
                document.close();
                reader.close();
                
            } catch (DocumentException | IOException e) {
                logger.error("Error occurred while adding text to margin: {}", e.getMessage());
            }
            
            return new ByteArrayInputStream(out.toByteArray());
        }

        class CustomWatermark extends PdfPageEventHelper {
            private String marginPosition;
            private String alignment;
            private String text;
            private float xPos = 0;
            private float yPos = 0;
    
            public CustomWatermark(String marginPosition, String alignment, String text) {
                this.marginPosition = marginPosition;
                this.alignment = alignment;
                this.text = text;
            }
    

            public void onEndPage(PdfWriter writer, Document document) {
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, Color.BLACK);
            
                switch (marginPosition.toLowerCase()) {
                    case "left":
                        xPos = document.left() - 20; // Adjust xPos to provide extra space
                        yPos = getYPosBasedOnAlignment(alignment, document);
                        break;
                    case "right":
                        xPos = document.right() + 20; // Adjust xPos to provide extra space
                        yPos = getYPosBasedOnAlignment(alignment, document);
                        break;
                    case "top":
                        xPos = getXPosBasedOnAlignment(alignment, document);
                        yPos = document.top() ; // Adjust yPos to avoid cutoff
                        break;
                    case "bottom":
                        xPos = getXPosBasedOnAlignment(alignment, document);
                        yPos = document.bottom() ; // Adjust yPos to avoid cutoff
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid margin position: " + marginPosition);
                }
            
                PdfContentByte canvas = writer.getDirectContent();
                canvas.beginText();
                canvas.setFontAndSize(font.getBaseFont(), font.getSize());
                canvas.setColorFill(Color.BLACK); // Ensure color contrast for visibility
            
                float rotationAngle = marginPosition.equals("left") ? 90 : (marginPosition.equals("right") ? -90 : 0);
                
                // Set scaling down the font size if text length is too long
                float textWidth = font.getBaseFont().getWidthPoint(text, font.getSize());
                if (textWidth > document.right() - document.left()) {
                    font.setSize(font.getSize() * (document.right() - document.left()) / textWidth);
                }
                
                // Display the text with the new adjusted position and rotation
                canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, text, xPos, yPos, rotationAngle);
            
                canvas.endText();
            }
            
            
    
            // Helper method to determine Y position based on alignment for left/right margin
            private float getYPosBasedOnAlignment(String alignment, Document document) {
                switch (alignment.toLowerCase()) {
                    case "top":
                        return document.top();
                    case "center":
                        return (document.top() + document.bottom()) / 2;
                    case "bottom":
                        return document.bottom();
                    default:
                        throw new IllegalArgumentException("Invalid alignment for left/right margin: " + alignment);
                }
            }
    
            // Helper method to determine X position based on alignment for top/bottom margin
            private float getXPosBasedOnAlignment(String alignment, Document document) {
                switch (alignment.toLowerCase()) {
                    case "left":
                        return document.left();
                    case "center":
                        return (document.left() + document.right()) / 2;
                    case "right":
                        return document.right();
                    default:
                        throw new IllegalArgumentException("Invalid alignment for top/bottom margin: " + alignment);
                }
            }
        }
    
    
    
}
