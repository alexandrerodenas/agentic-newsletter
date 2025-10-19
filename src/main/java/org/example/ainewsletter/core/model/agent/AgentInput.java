package org.example.ainewsletter.core.model.agent;

import lombok.NonNull;

public record AgentInput<T>(@NonNull T input) {
}
