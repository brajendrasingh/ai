package com.bksoft.etl.controller;

import com.bksoft.etl.service.PdfIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/read")
    public ResponseEntity<String> read(@RequestParam String fileName) throws Exception {
        ingestionService.ingest(null, fileName);
        return ResponseEntity.ok("Document read successfully");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws Exception {
        Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
        file.transferTo(tempFile);
        ingestionService.ingest(tempFile, null);
        Files.deleteIfExists(tempFile);
        return ResponseEntity.ok("Document ingested successfully");
    }
}
