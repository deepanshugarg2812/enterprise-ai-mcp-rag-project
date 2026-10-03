package com.main.rag;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RagClient {

    private final RestClient restClient;

    public RagClient(
            RestClient.Builder builder,
            @Value("${rag.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Tool(
            name = "search_knowledge_base",
            description = """
                Search the enterprise knowledge base.

                Use this tool when the user asks about:
                - company policies
                - business rules
                - documentation
                - procedures

                The input must be a plain text search query.

                Operation: READ.
                Side effects: NONE.
                """
    )
    public String searchKnowledgeBase(
            @ToolParam(
                    description = "Plain text search query. Example: cancellation policy",
                    required = true
            )
            String query) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rag/search")
                        .queryParam("query", query)
                        .queryParam("topK", 5)
                        .build())
                .retrieve()
                .body(String.class);
    }
}