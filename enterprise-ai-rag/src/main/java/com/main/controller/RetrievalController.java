package com.main.controller;

import com.main.service.VectorRetrievalService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RetrievalController {

    private final VectorRetrievalService retrievalService;

    public RetrievalController(
            VectorRetrievalService retrievalService) {

        this.retrievalService = retrievalService;
    }

    @GetMapping("/search")
    public List<Document> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {

        return retrievalService.search(
                query,
                topK
        );
    }
}