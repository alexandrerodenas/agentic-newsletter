package org.example.ainewsletter.infra.agent;

import java.util.function.Function;
import org.springframework.ai.chat.prompt.Prompt;

public interface PromptProvider extends Function<AgentInput<?>, Prompt> {

}
