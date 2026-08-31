package com.bksoft.rag.controller;

import com.bksoft.rag.dto.DocumentRequest;
import com.bksoft.rag.dto.SearchResponse;
import com.bksoft.rag.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<String> addDocument(@RequestBody DocumentRequest request) {
        documentService.addDocument(request.text());
        return ResponseEntity.ok("Document added successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResponse>> search(@RequestParam String query, @RequestParam(defaultValue = "5") int topK) {
        return ResponseEntity.ok(documentService.search(query, topK));
    }
}
