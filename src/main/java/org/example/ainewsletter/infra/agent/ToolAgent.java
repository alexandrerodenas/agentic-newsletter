package org.example.ainewsletter.infra.agent;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.agent.AgentInput;
import org.example.ainewsletter.core.model.agent.AgentOutput;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;

@Slf4j
public final class ToolAgent implements Agent {

    @Getter
    private final String name;
    private final PromptProvider promptProvider;
    private final ChatModel chatModel;
    private final List<ToolCallback> toolCallbacks;

    public ToolAgent(
        @NonNull final String name,
        @NonNull final PromptProvider promptProvider,
        @NonNull final ChatModel chatModel,
        @NonNull final List<ToolCallback> toolCallbacks
    ) {
        if(name.isBlank()) {
            throw new IllegalArgumentException("Agent name cannot be blank");
        }
        this.name = name;
        this.promptProvider = promptProvider;
        this.chatModel = chatModel;
        this.toolCallbacks = toolCallbacks;
    }

    public AgentOutput execute(final AgentInput<?> agentInput) {
        log.debug("Submitting input to agent {}", name.toLowerCase());

        final ChatResponse response = ChatClient.create(chatModel)
            .prompt(promptProvider.apply(agentInput))
            .toolCallbacks(toolCallbacks)
            .call()
            .chatResponse();
        log.debug("Received response from agent {}", name.toLowerCase());

        assert response != null;
        return new AgentOutput(
            this.name,
            Optional.ofNullable(
                response.getResult().getOutput().getText()
            ).orElse("Sorry, I'm not able to answer your question.").trim()
        );
    }

}
