package com.main.service;

import com.main.rag.RagClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;
    private final RagClient ragClient;

    public AgentService(
            ChatClient chatClient,
            ToolCallbackProvider mcpTools,
            RagClient ragClient) {

        this.chatClient = chatClient;
        this.mcpTools = mcpTools;
        this.ragClient = ragClient;
    }

    public String ask(String question) {

        return chatClient
                .prompt()
                .system("""
                        You are an enterprise operations assistant.

                        You can use tools to retrieve enterprise data.

                        Rules:
                        - Do not invent business data.
                        - Use MCP tools for live business/customer/order data.
                        - Use the knowledge-base tool for enterprise policies,
                          documentation, and business rules.
                        - When tool results are available, base your answer
                          on those results.
                        - If the available tools do not provide enough
                          information, clearly say so.
                        """)
                .user(question)
                .tools(mcpTools, ragClient)
                .call()
                .content();
    }
}