package org.example.ainewsletter.infra.news;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.news.News;
import org.example.ainewsletter.core.news.services.PressReviewer;
import org.example.ainewsletter.infra.agent.Agent;
import org.example.ainewsletter.infra.agent.AgentInput;
import org.example.ainewsletter.infra.agent.AgentOutput;

@Slf4j
public class AiPressReviewer implements PressReviewer {

    private final Agent pressReviewerAgent;

    public AiPressReviewer(final Agent pressReviewerAgent) {
        this.pressReviewerAgent = pressReviewerAgent;
    }


    @Override
    public String review(final List<News> news) {
        final AgentOutput output = pressReviewerAgent.execute(new AgentInput<>(news));
        log.info("Press review created successfully");

        return output.content();
    }
}
