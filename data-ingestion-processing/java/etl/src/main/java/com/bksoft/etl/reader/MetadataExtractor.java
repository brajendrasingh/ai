package com.bksoft.etl.reader;

import com.bksoft.etl.constants.MetadataKeys;
import com.bksoft.etl.model.DocumentSource;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetadataExtractor {

    public MetadataExtractor() {
    }

    public List<Document> enrich(List<Document> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            Document chunk = chunks.get(i);
            String documentId = (String) chunk.getMetadata().get("documentId");
            chunk.getMetadata().put("chunkIndex", i);
            chunk.getMetadata().put("chunkId", documentId + "-" + String.format("%05d", i));
        }
        return chunks;
    }

    public List<Document> extract(List<Document> documents, DocumentSource source) {
        for (Document document : documents) {
            document.getMetadata().put(MetadataKeys.DOCUMENT_ID, source.documentId());
            document.getMetadata().put(MetadataKeys.FILE_NAME, source.fileName());
            document.getMetadata().put("contentType", source.contentType());
            document.getMetadata().put("fileSize", source.fileSize());
            document.getMetadata().put("checksum", source.checksum());
        }
        return documents;
    }
}
