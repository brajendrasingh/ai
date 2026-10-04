package com.bksoft.etl.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class PdfReader {

    public PdfReader() {
    }

    public List<Document> read(Path file) {
        Resource resource = new FileSystemResource(file);
        TikaDocumentReader documentReader = new TikaDocumentReader(resource);
        List<Document> rawDocuments = documentReader.read();
        return rawDocuments;
    }

    public List<Document> read(Resource pdfResource) {
        TikaDocumentReader documentReader = new TikaDocumentReader(pdfResource);
        return documentReader.read();
    }

}
