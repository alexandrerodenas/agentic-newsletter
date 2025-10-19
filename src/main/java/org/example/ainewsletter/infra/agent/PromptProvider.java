package org.example.ainewsletter.infra.agent;

import java.util.function.Function;
import org.example.ainewsletter.core.model.agent.AgentInput;
import org.springframework.ai.chat.prompt.Prompt;

public interface PromptProvider extends Function<AgentInput, Prompt> {

}
