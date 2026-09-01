package com.bksoft.rag.dto;

import java.util.Map;

public record SearchResponse(String id, String text, double score, Map<String, Object> metadata) {
}
