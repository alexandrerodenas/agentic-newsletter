package org.example.ainewsletter.core.model.agent;

import lombok.NonNull;

public record AgentOutput(@NonNull String agentName, @NonNull String content) {

    public AgentOutput {
        if(agentName.isBlank()) {
            throw new IllegalArgumentException("Agent name cannot be blank");
        }
        if(content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
    }
}
