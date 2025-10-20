package org.example.ainewsletter.infra.agent;

public interface Agent {

    AgentOutput execute(AgentInput<?> input);

}
