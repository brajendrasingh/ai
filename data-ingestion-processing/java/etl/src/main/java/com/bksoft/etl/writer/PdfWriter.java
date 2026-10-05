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
        long milliSecond = end - start;
        log.info("vectorStore.add() completed. documents={}, time={} Milli Second, TimeInSecond={}, Minutes={}", documents.size(), milliSecond, milliSecond/1000, milliSecond/60000);
        System.out.println("Successfully ingested PDF and loaded " + documents.size() + " text chunks into the Vector Database!");
    }

    public void writeAndEvictStaleVersions(List<Document> documents, String docId, long currentVersion) {
        //Commit new content
        vectorStore.add(documents);
        String deleteExpression = String.format("doc_id == '%s' && doc_version < %d", docId, currentVersion);
        //Evicting stale records via expression
        vectorStore.delete(deleteExpression);
    }
}
