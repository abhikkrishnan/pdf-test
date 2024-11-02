package com.pdf.htmltopdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class htmltopdfService {

    public static void main(String[] args) throws IOException {
        String html = "<h1>hello</h1>";
        String xhtml = htmlToXhtml(html);
        xhtmlToPdf(xhtml, "output.pdf");
    }

    private static String htmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    private static void xhtmlToPdf(String xhtml, String outFileName) throws IOException {
        File output = new File(outFileName);
        ITextRenderer iTextRenderer = new ITextRenderer();
        iTextRenderer.setDocumentFromString(xhtml);
        iTextRenderer.layout();
        OutputStream os = new FileOutputStream(output);
        iTextRenderer.createPDF(os);
        os.close();
    }
    
}
