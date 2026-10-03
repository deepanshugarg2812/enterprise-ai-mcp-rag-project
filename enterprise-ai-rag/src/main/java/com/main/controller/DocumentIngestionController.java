package com.main.controller;

import com.main.service.RagIngestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag/documents")
public class DocumentIngestionController {

    private final RagIngestionService ingestionService;

    public DocumentIngestionController(
            RagIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public void ingest(@RequestBody IngestDocumentRequest request) {

        ingestionService.ingest(
                request.content(),
                request.source()
        );
    }

    public record IngestDocumentRequest(
            String source,
            String content
    ) {
    }
}