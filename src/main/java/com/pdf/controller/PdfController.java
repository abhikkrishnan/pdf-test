package com.pdf.controller;

import java.io.ByteArrayInputStream;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import  com.lowagie.text.Rectangle;
import com.lowagie.text.PageSize;
import com.pdf.services.PdfService;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("api/v0/createPdf")
    public ResponseEntity<InputStreamResource> createPdf() {

        ByteArrayInputStream pdf = pdfService.createPdf(false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=test.pdf");
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

    @PostMapping("api/v0/createPdf_merged")
    public ResponseEntity<InputStreamResource> createPdf_merged() {

        ByteArrayInputStream pdf = pdfService.createPdf(true);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=test.pdf");
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

    @PostMapping("api/v0/manipulatePdf")
    public ResponseEntity<InputStreamResource> manipulatepdf(@RequestParam String psize) {

        Rectangle pageSize = mapPageSize(psize);

        if (pageSize == null) {
            return ResponseEntity.badRequest().body(null); // Return error if page size is not valid
        }

        ByteArrayInputStream pdf = pdfService.manipulate_pdf(pageSize,true);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=test.pdf");
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

    // Helper method to map the psize string to a Rectangle object
    private Rectangle mapPageSize(String psize) {
        switch (psize.toUpperCase()) {
            case "A1": return PageSize.A1;
            case "A2": return PageSize.A2;
            case "A3": return PageSize.A3;
            case "A4": return PageSize.A4;
            case "A5": return PageSize.A5;
            case "A6": return PageSize.A6;
            default: return null; // Return null for invalid page size
        }
    }

    @PostMapping("api/v0/add_margin")
    public ResponseEntity<InputStreamResource> add_margin_pdf(@RequestParam Integer left_margin) {

       ByteArrayInputStream pdf = pdfService.add_margin( left_margin);
       HttpHeaders httpHeaders = new HttpHeaders();
       httpHeaders.add("Content-Disposition", "inline; filename=test.pdf");
       return ResponseEntity
               .ok()
               .headers(httpHeaders)
               .contentType(MediaType.APPLICATION_PDF)
               .body(new InputStreamResource(pdf));
   }


}
