package com.main.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorRetrievalService {

    private final VectorStore vectorStore;

    public VectorRetrievalService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> search(
            String query,
            int topK) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return vectorStore.similaritySearch(
                request
        );
    }
}