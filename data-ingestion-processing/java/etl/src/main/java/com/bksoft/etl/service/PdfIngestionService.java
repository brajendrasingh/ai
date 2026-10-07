package com.bksoft.etl.service;

import com.bksoft.etl.entities.DocumentEntity;
import com.bksoft.etl.entities.DocumentVersionEntity;
import com.bksoft.etl.model.DocumentSource;
import com.bksoft.etl.model.VersionStatus;
import com.bksoft.etl.reader.MetadataExtractor;
import com.bksoft.etl.reader.PdfReader;
import com.bksoft.etl.repository.DocumentRepository;
import com.bksoft.etl.repository.DocumentVersionRepository;
import com.bksoft.etl.transformer.PdfTransformer;
import com.bksoft.etl.utils.FileUtils;
import com.bksoft.etl.writer.PdfWriter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PdfIngestionService {
    @Value("${app.ingestion.data-dir}")
    private String dataDir;

    private final PdfReader reader;
    private final PdfTransformer transformer;
    private final PdfWriter pdfWriter;
    private final MetadataExtractor metadataExtractor;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;

    public PdfIngestionService(PdfReader reader, PdfTransformer transformer, PdfWriter pdfWriter, MetadataExtractor metadataExtractor,
                               DocumentVersionRepository versionRepository, DocumentRepository documentRepository) {
        this.reader = reader;
        this.transformer = transformer;
        this.pdfWriter = pdfWriter;
        this.metadataExtractor = metadataExtractor;
        this.versionRepository = versionRepository;
        this.documentRepository = documentRepository;
    }

    public void ingest(Path filePath, String fileName) throws Exception {
        if (fileName != null) {
            Resource resource = new FileSystemResource(dataDir + "/incoming/" + fileName);
            // 1. EXTRACT: Read the raw PDF file
            List<Document> rawDocuments = reader.read(resource);

            // 2. Metadata
            //rawDocuments = metadataExtractor.extract(rawDocuments, new DocumentSource("", "", "", 1L, ""));

            // 3. TRANSFORM: Split text into smaller chunks
            List<Document> splitDocuments = transformer.transform(rawDocuments);

            // 4. LOAD/WRITE: Generate embeddings & save to vector DB
            pdfWriter.writeToVectorDb(splitDocuments);
        }
    }

    public void ingestVersionedData(Path filePath, String fileName) throws Exception {
        Resource resource = new FileSystemResource(dataDir + "/incoming/" + fileName);
        String fileHash = new FileUtils().calculateSha256(resource);
        String documentId = UUID.randomUUID().toString();
        int version = 1;
        if (versionRepository.existsByChecksum(fileHash)) {
            System.out.println("Document already exists: " + fileName + ", hash=" + fileHash);
            return;
        } else if (documentRepository.existsByFileName(fileName)) { //Check whether this exact file version already exists in database
            DocumentEntity document = documentRepository.findByFileName(fileName).get();
            DocumentVersionEntity de = versionRepository.findTopByDocumentIdOrderByVersionDesc(document.getId()).get();
            version = de.getVersion() + 1;
            DocumentVersionEntity dve = DocumentVersionEntity.builder().document(document).version(version)
                    .checksum(fileHash).filePath(fileName).status(VersionStatus.INGESTED).createdAt(Instant.now()).build();
            versionRepository.save(dve);

            document.setLatestVersion(version);
            document.setUpdatedAt(Instant.now());
            documentRepository.save(document);
        } else {
            DocumentEntity de = DocumentEntity.builder().documentId(documentId).fileName(fileName).latestVersion(version)
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            documentRepository.save(de);
            DocumentVersionEntity dve = DocumentVersionEntity.builder().document(de).version(version)
                    .checksum(fileHash).filePath(fileName).status(VersionStatus.INGESTED).createdAt(Instant.now()).build();
            versionRepository.save(dve);
        }
        String contentType = Files.probeContentType(resource.getFile().toPath());
        // 1. EXTRACT: Read the raw PDF file
        List<Document> rawDocuments = reader.read(new FileSystemResource(dataDir + "/incoming/" + fileName));

        // 2. Metadata
        rawDocuments = metadataExtractor.extract(rawDocuments, new DocumentSource(documentId, fileName, version, contentType, resource.contentLength(), fileHash));

        // 3. TRANSFORM: Split text into smaller chunks
        List<Document> splitDocuments = transformer.transform(rawDocuments);

        // 4. LOAD/WRITE: Generate embeddings & save to vector DB
        pdfWriter.writeAndEvictStaleVersions(splitDocuments, documentId, version);
    }
}
