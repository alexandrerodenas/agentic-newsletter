package org.example.ainewsletter.infra.news;

import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.news.services.NewsletterFormatter;
import org.example.ainewsletter.infra.agent.Agent;
import org.example.ainewsletter.infra.agent.AgentInput;
import org.example.ainewsletter.infra.agent.AgentOutput;

@Slf4j
public class AiNewsletterFormatter implements NewsletterFormatter {

    private final Agent newsletterFormatterAgent;

    public AiNewsletterFormatter(final Agent newsletterFormatterAgent) {
        this.newsletterFormatterAgent = newsletterFormatterAgent;
    }

    @Override
    public String format(final String content) {
        final AgentOutput output = newsletterFormatterAgent.execute(new AgentInput<>(content));
        log.info("Newsletter created successfully");

        return output.content()
            .replace("```html", "")
            .replace("```", "")
            .trim();
    }
}
