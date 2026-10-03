package com.main.service;

import com.main.util.DocumentChunker;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagIngestionService {

    private final DocumentChunker chunker;
    private final VectorStoreService vectorStoreService;

    public RagIngestionService(
            DocumentChunker chunker,
            VectorStoreService vectorStoreService) {

        this.chunker = chunker;
        this.vectorStoreService = vectorStoreService;
    }

    public void ingest(
            String content,
            String source) {

        Document document = new Document(
                content,
                Map.of(
                        "source", source,
                        "documentType", "business-document"
                )
        );

        List<Document> chunks =
                chunker.chunk(List.of(document));

        vectorStoreService.store(chunks);
    }
}