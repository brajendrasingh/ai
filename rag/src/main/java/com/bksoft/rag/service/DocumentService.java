package com.bksoft.rag.service;

import com.bksoft.rag.dto.SearchResponse;

import java.util.List;

public class DocumentService {

    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addDocument(String text) {
        Document document = new Document(text);
        vectorStore.add(List.of(document));
    }

    public List<SearchResponse> search(String query, int topK) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build());

        return documents.stream().map(document -> new SearchResponse(
                        document.getId(), document.getText(), document.getScore() != null ? document.getScore() : 0.0)).toList();
    }
}
