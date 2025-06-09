package com.example.mc_filesystem_client;


import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class McpAskController {

    private final ChatClient chatClient;

    public McpAskController(ChatClient.Builder chatClientBuilder,
                            SyncMcpToolCallbackProvider mcpSyncClients) { //
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(mcpSyncClients.getToolCallbacks())  //
                .build();
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        return chatClient.prompt()
                .user(question.question())
                .call()
                .entity(Answer.class);
    }

    public record Question(String question) { }

    public record Answer(String answer) { }

}