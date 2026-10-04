package com.bksoft.etl.controller;

import com.bksoft.etl.service.PdfIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RequestMapping("/api/documents")
@RestController
public class IngestionController {
    private PdfIngestionService ingestionService;

    public IngestionController(PdfIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws Exception {
        Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
        file.transferTo(tempFile);
        ingestionService.ingest(tempFile);
        Files.deleteIfExists(tempFile);
        return ResponseEntity.ok("Document ingested successfully");
    }
}
