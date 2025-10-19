package org.example.ainewsletter.core.model.agent;

import lombok.NonNull;

public record AgentInput(@NonNull String content) {

    public AgentInput {
        if(content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
    }
}
