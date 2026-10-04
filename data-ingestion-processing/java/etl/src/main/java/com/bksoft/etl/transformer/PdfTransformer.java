package com.bksoft.etl.transformer;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfTransformer {

    public List<Document> transform(List<Document> documents) {
        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(800)                  // Size of each text block in tokens
                .withMinChunkSizeChars(100)          // Minimum characters per block
                .withMinChunkLengthToEmbed(10)       // Minimum block length required to embed
                .withMaxNumChunks(10000)             // Maximum number of chunks allowed
                .withKeepSeparator(true)             // Retain separators/punctuation boundaries
                .build();
        return textSplitter.split(documents);
    }
}
