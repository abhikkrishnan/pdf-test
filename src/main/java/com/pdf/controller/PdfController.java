package com.pdf.controller;

import java.io.ByteArrayInputStream;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdf.services.PdfService;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/createPdf")
    public ResponseEntity<InputStreamResource> createPdf() {

        ByteArrayInputStream pdf = pdfService.createPdf();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline; filename=springboot.pdf");
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

}
