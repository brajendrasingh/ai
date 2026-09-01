package com.bksoft.rag.service;

import com.bksoft.rag.dto.DocumentRequest;
import com.bksoft.rag.dto.SearchResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {

    @Value("${app.rag.similarity-threshold:0.7}")
    private double similarityThreshold;

    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addDocument(DocumentRequest request) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", request.source());
        metadata.put("category", request.category());
        Document document = new Document(request.text(), metadata);
        vectorStore.add(List.of(document));
    }

    public List<SearchResponse> search(String query, int topK) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(topK).similarityThreshold(similarityThreshold).build());

        return documents.stream().map(document -> new SearchResponse(
                document.getId(), document.getText(), document.getScore() != null ? document.getScore() : 0.0, document.getMetadata())).toList();
    }
}
