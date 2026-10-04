package com.bksoft.etl.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PdfWriter {
    private final VectorStore vectorStore;

    public PdfWriter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void writeToVectorDb(List<Document> documents) {
        long start = System.currentTimeMillis();
        vectorStore.add(documents);
        long end = System.currentTimeMillis();
        log.info("vectorStore.add() completed. documents={}, time={} ms", documents.size(), end - start);
        System.out.println("Successfully ingested PDF and loaded " + documents.size() + " text chunks into the Vector Database!");
    }
}
