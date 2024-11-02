package com.pdf.htmltopdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@RestController
public class htmltopdfController {

    @Autowired
    private htmltopdfService pdfservice;



    @GetMapping("/generate-html-pdf")
    public ResponseEntity<InputStreamResource> generate_html_Pdf(@RequestParam String htmlFilePath) throws IOException {
        String xhtml = htmlToXhtml(htmlFilePath);
        InputStream pdfStream = xhtmlToPdf(xhtml);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=output.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    private String htmlToXhtml(String htmlFilePath) throws IOException {
        // Read the HTML content from a local file
        String html = new String(Files.readAllBytes(Paths.get(htmlFilePath)));

        // Convert HTML to XHTML
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    private InputStream xhtmlToPdf(String xhtml) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer iTextRenderer = new ITextRenderer();

        // Add font resolver if custom fonts are required

        iTextRenderer.setDocumentFromString(xhtml);
        iTextRenderer.layout();
        iTextRenderer.createPDF(os);

        return new ByteArrayInputStream(os.toByteArray());
    }
        
}
