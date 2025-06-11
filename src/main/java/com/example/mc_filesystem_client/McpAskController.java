package com.example.mc_filesystem_client;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
public class McpAskController {

    private final ChatClient chatClient;
    @Value("classpath:/prompts/systemPrompt.st")
    private Resource systemPromptTemplate;

    public McpAskController(ChatClient.Builder chatClientBuilder,
                            SyncMcpToolCallbackProvider mcpSyncClients) { //
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(mcpSyncClients.getToolCallbacks())  //
                .build();
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        final var now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                .withZone(ZoneId.systemDefault());
        final String formatteNow  = formatter.format(now);
        return chatClient.prompt()
                .system(sys ->
                        sys.text(systemPromptTemplate)
                                .param("todaysDate", formatteNow))
                .user(question.question)
                .call()
                .entity(Answer.class);
    }

    public record Question(String question) { }

    public record Answer(String answer) { }

}