package com.bksoft.etl.model;

public record DocumentSource(String documentId, String fileName, int version,
                             String contentType, long fileSize, String checksum) {
}
