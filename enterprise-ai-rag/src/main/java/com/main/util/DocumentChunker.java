package com.main.util;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentChunker {
    private final TokenTextSplitter splitter;

    public DocumentChunker() {
        this.splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(200)
                .withMinChunkLengthToEmbed(20)
                .withKeepSeparator(true)
                .build();
    }

    public List<Document> chunk(List<Document> documents) {
        return splitter.apply(documents);
    }
}
