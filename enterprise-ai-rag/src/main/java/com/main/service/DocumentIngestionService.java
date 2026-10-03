package com.main.service;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {
    public List<Document> createDocument(String content, String source) {
        Document document = new Document(content, Map.of("source", source));
        return List.of(document);
    }
}
