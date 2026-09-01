package com.bksoft.rag.service;

import com.bksoft.rag.dto.DocumentRequest;
import com.bksoft.rag.dto.SearchResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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

    public void addDocumentUsingChunks(DocumentRequest request) {
        // 1. Create metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", request.source());
        metadata.put("category", request.category());

        // 2. Create original document
        Document document = new Document(request.text(), metadata);

        // 3. Create text splitter
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(500).withMinChunkSizeChars(100).withKeepSeparator(true).build();

        // 4. Split document into chunks
        List<Document> chunks = splitter.apply(List.of(document));

        // 5. Store chunks in Milvus
        vectorStore.add(chunks);
    }

    public List<SearchResponse> search(String query, int topK) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(topK).similarityThreshold(similarityThreshold).build());

        return documents.stream().map(document -> new SearchResponse(
                document.getId(), document.getText(), document.getScore() != null ? document.getScore() : 0.0, document.getMetadata())).toList();
    }
}
