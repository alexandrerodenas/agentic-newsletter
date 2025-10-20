package org.example.ainewsletter.infra.agent;

import lombok.NonNull;

public record AgentInput<T>(@NonNull T input) {
}
