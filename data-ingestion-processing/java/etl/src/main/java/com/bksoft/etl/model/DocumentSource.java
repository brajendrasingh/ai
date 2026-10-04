package com.bksoft.etl.model;

public record DocumentSource(String documentId, String fileName,
                             String contentType, long fileSize, String checksum) {
}
