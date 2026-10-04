package com.bksoft.etl.writer;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfWriter {
    private final VectorStore vectorStore;

    public PdfWriter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void writeToVectorDb(List<Document> documents) {
        vectorStore.add(documents); //vectorStore.accept(documents);
        System.out.println("Successfully ingested PDF and loaded " + documents.size() + " text chunks into the Vector Database!");
    }
}
