package com.bksoft.etl.service;

import com.bksoft.etl.reader.PdfReader;
import com.bksoft.etl.transformer.PdfTransformer;
import com.bksoft.etl.writer.PdfWriter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class PdfIngestionService {
    @Value("${app.ingestion.data-dir}")
    private String dataDir;

    private final PdfReader reader;
    private final PdfTransformer transformer;
    private final PdfWriter pdfWriter;

    public PdfIngestionService(PdfReader reader, PdfTransformer transformer, PdfWriter pdfWriter) {
        this.reader = reader;
        this.transformer = transformer;
        this.pdfWriter = pdfWriter;
    }

    public void ingest(Path filePath, String fileName) throws Exception {
        if (fileName != null) {
            Resource resource = new FileSystemResource(dataDir + "/incoming/" + fileName);
            // 1. EXTRACT: Read the raw PDF file
            List<Document> rawDocuments = reader.read(resource);

            // 2. TRANSFORM: Split text into smaller chunks
            List<Document> splitDocuments = transformer.transform(rawDocuments);

            // 3. LOAD/WRITE: Generate embeddings & save to vector DB
            pdfWriter.writeToVectorDb(splitDocuments);
        }
    }
}
