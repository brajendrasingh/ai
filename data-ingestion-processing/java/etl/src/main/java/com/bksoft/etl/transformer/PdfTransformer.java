package com.bksoft.etl.transformer;

import com.bksoft.etl.constants.MetadataKeys;
import com.bksoft.etl.utils.FileUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfTransformer {

    private FileUtils fileUtils;

    public PdfTransformer(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public List<Document> transform(List<Document> documents) {
        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(800)                  // Size of each text block in tokens
                .withMinChunkSizeChars(100)          // Minimum characters per block
                .withMinChunkLengthToEmbed(10)       // Minimum block length required to embed
                .withMaxNumChunks(10000)             // Maximum number of chunks allowed
                .withKeepSeparator(true)             // Retain separators/punctuation boundaries
                .build();
        List<Document> splittedChunks = textSplitter.split(documents);
        for (Document chunk : splittedChunks) {
            String chuckText = chunk.getText();
            String chunkHash = fileUtils.generateSha256(chuckText);
            chunk.getMetadata().put("doc_id", chunk.getMetadata().get(MetadataKeys.DOCUMENT_ID));
            chunk.getMetadata().put("doc_version", chunk.getMetadata().get("version"));
            chunk.getMetadata().put("chunk_hash", chunkHash);
        }
        return splittedChunks;
    }
}
