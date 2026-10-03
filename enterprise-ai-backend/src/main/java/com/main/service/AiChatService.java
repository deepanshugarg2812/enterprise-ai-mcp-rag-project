package com.main.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}