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
import com.lowagie.text.Image;
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

    
        private void addContent(PdfContentByte canvas, Document document, String content, String position, String alignment, String type, float fontSize) {
            float x = 0, y = 0, rotation = 0;
    
            // Set coordinates based on position and alignment
            switch (position) {
                case "top": y = document.top() - fontSize - 5; break;
                case "center": y = (document.top() + document.bottom()) / 2; break;
                case "bottom": y = document.bottom() + 5; break;
            }
    
            switch (alignment) {
                case "left": x = document.left() + 40; rotation = position.equals("center") ? 90 : 0; break;
                case "right": x = document.right() - 40; rotation = position.equals("center") ? -90 : 0; break;
                case "center": x = (document.left() + document.right()) / 2; break;
            }
    
            try {
                if (type.equals("text")) {
                    // Add text content
                    Phrase phrase = new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA, fontSize));
                    canvas.beginText();
                    canvas.setFontAndSize(phrase.getFont().getBaseFont(), fontSize);
                    canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, phrase.getContent(), x, y, rotation);
                    canvas.endText();
                } else if (type.equals("image")) {
                    // Add image content
                    Image image = Image.getInstance(content);
                    image.scaleToFit(fontSize * 2, fontSize * 2); // Adjust scaling as needed
                    image.setAbsolutePosition(x, y);
                    image.setRotationDegrees(rotation);
                    canvas.addImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        private float calculateFontSize(Rectangle pageSize) {
            float diagonal = (float) Math.sqrt(Math.pow(pageSize.getWidth(), 2) + Math.pow(pageSize.getHeight(), 2));
            float minDiagonal = 300;
            float maxDiagonal = 2400;
            float minFontSize = 9;
            float maxFontSize = 32;
            diagonal = Math.max(minDiagonal, Math.min(maxDiagonal, diagonal));
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

    public ByteArrayInputStream add_margin(Float left_margin, Float right_margin, Float top_margin, Float bottom_margin) {

        left_margin *=72;
        right_margin*=72;
        top_margin*=72;
        bottom_margin*=72;


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);


    
        PdfWriter writer = PdfWriter.getInstance(document, out);
        float translationX = 0.0f;
        float translationY = 0.0f;


        Watermark event = new Watermark();
        writer.setPageEvent(event);

    
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
        public ByteArrayInputStream addTextToMargin(String marginPosition, String alignment, String text, Boolean image) {
            // logger.info("Adding text to margin: Position - {}, Alignment - {}, Text - {}, Isimage-{}", marginPosition, alignment, text,image);

            ByteArrayInputStream pdfWithMargins;


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            try {
                // pdf with margin 
                if (marginPosition.equalsIgnoreCase("left")) {
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
                
                if (image) {
                    CustomWatermark watermark = new CustomWatermark(marginPosition, alignment,"C:\\Users\\user\\Downloads\\frame.png",true);
                    writer.setPageEvent(watermark);
                }else{
                    CustomWatermark watermark = new CustomWatermark(marginPosition, alignment, text);
                    writer.setPageEvent(watermark);
                }
                // Set custom watermark event to add text in margin

    
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
            private String imagePath; // New field for image path
            private boolean isImage; // Flag to check if watermark is image or text
            private float xPos = 0;
            private float yPos = 0;
        
            // Constructor for text watermark
            public CustomWatermark(String marginPosition, String alignment, String text) {
                this.marginPosition = marginPosition;
                this.alignment = alignment;
                this.text = text;
                this.isImage = false; // This is a text watermark
            }
        
            // Constructor for image watermark
            public CustomWatermark(String marginPosition, String alignment, String imagePath, boolean isImage) {
                this.marginPosition = marginPosition;
                this.alignment = alignment;
                this.imagePath = imagePath;
                this.isImage = isImage; // This is an image watermark

            }
        
            public void onEndPage(PdfWriter writer, Document document) {
                if (isImage) {
                    addImageWatermark(writer, document);
                } else {
                    addTextWatermark(writer, document);
                }
            }
        
            // Method to add text watermark
            private void addTextWatermark(PdfWriter writer, Document document) {
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        
                setPosition(document); // Set xPos and yPos based on marginPosition and alignment
        
                PdfContentByte canvas = writer.getDirectContent();
                canvas.beginText();
                canvas.setFontAndSize(font.getBaseFont(), font.getSize());
                canvas.setColorFill(Color.BLACK); // Set text color
                
                float rotationAngle = marginPosition.equals("left") ? 90 : (marginPosition.equals("right") ? -90 : 0);
                canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, text, xPos, yPos, rotationAngle);
                
                canvas.endText();
            }
        
            // Method to add image watermark
            private void addImageWatermark(PdfWriter writer, Document document) {
                try {
                    Image image = Image.getInstance(imagePath); // Load image from path
            
                    setPosition(document); // Set xPos and yPos based on marginPosition and alignment
            
                    // Calculate the margin area dimensions
                    float maxWidth, maxHeight;
                    switch (marginPosition.toLowerCase()) {
                        case "left":
                        case "right":
                            maxWidth = document.leftMargin();
                            maxHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
                            break;
                        case "top":
                        case "bottom":
                            maxWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                            maxHeight = document.topMargin();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid margin position: " + marginPosition);
                    }
            
                    // Calculate scaling to fit the image within the margin area
                    float widthScale = maxWidth / image.getWidth();
                    float heightScale = maxHeight / image.getHeight();
                    float scale = Math.min(widthScale, heightScale); // Use the smaller scale to fit
            
                    image.scaleAbsolute(image.getWidth() * scale, image.getHeight() * scale);
            
                    // Adjust yPos for "top" and "bottom" to ensure it fits within bounds
                    if (marginPosition.equalsIgnoreCase("top")) {
                        yPos = document.top() - (image.getScaledHeight() / 2);
                    } else if (marginPosition.equalsIgnoreCase("bottom")) {
                        yPos = document.bottom() + (image.getScaledHeight() / 2);
                    }
            
                    // Adjust xPos for "center" alignment if specified
                    if (alignment.equalsIgnoreCase("center")) {
                        xPos = (document.left() + document.right() - image.getScaledWidth()) / 2;
                    }
            
                    // Set rotation if needed
                    float rotationAngle = marginPosition.equalsIgnoreCase("left") ? 90 : (marginPosition.equalsIgnoreCase("right") ? -90 : 0);
                    image.setRotationDegrees(rotationAngle);
            
                    // Set final position and add the image to the PDF
                    image.setAbsolutePosition(xPos, yPos);
                    PdfContentByte canvas = writer.getDirectContent();
                    canvas.addImage(image);
            
                    // Debugging confirmation
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            
            
        
            // Helper method to determine position based on alignment and marginPosition
            private void setPosition(Document document) {
                switch (marginPosition.toLowerCase()) {
                    case "left":
                        xPos = document.left() - 20;
                        yPos = getYPosBasedOnAlignment(alignment, document);
                        break;
                    case "right":
                        xPos = document.right() + 20;
                        yPos = getYPosBasedOnAlignment(alignment, document);
                        break;
                    case "top":
                        xPos = getXPosBasedOnAlignment(alignment, document);
                        yPos = document.top() + 20;
                        break;
                    case "bottom":
                        xPos = getXPosBasedOnAlignment(alignment, document);
                        yPos = document.bottom() - 20;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid margin position: " + marginPosition);
                }
            }
        
            // Helper method to determine Y position based on alignment for left/right margin
            private float getYPosBasedOnAlignment(String alignment, Document document) {
                switch (alignment.toLowerCase()) {
                    case "top":
                        return document.top()-(text.length()*2);
                    case "center":
                        return (document.top() + document.bottom()) / 2;
                    case "bottom":
                        return document.bottom()+(text.length()*2);
                    default:
                        throw new IllegalArgumentException("Invalid alignment for left/right margin: " + alignment);
                }
            }
        
            // Helper method to determine X position based on alignment for top/bottom margin
            private float getXPosBasedOnAlignment(String alignment, Document document) {
                switch (alignment.toLowerCase()) {
                    case "left":
                        return document.left()+(text.length()*2);
                    case "center":
                        return (document.left() + document.right()) / 2;
                    case "right":
                        return document.right()-(text.length()*2);
                    default:
                        throw new IllegalArgumentException("Invalid alignment for top/bottom margin: " + alignment);
                }
            }
        }
        
    
    
    
}
