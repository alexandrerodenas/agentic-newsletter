package org.example.ainewsletter.core.use_cases;

import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.agent.AgentOutput;
import org.example.ainewsletter.core.model.agent.AgentInput;
import org.example.ainewsletter.core.model.news.News;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.SourceToClient;

@Slf4j
public final class CreateNewsletter {

    private final SourceToClient sourceToClient;
    private final Agent sourceFetcherAgent;
    private final Agent summaryAgent;
    private final Agent newsletterAgent;

    public CreateNewsletter(
        final SourceToClient sourceToClient,
        final Agent sourceFetcherAgent,
        final Agent summaryAgent,
        final Agent newsletterAgent
    ) {
        this.sourceToClient = sourceToClient;
        this.sourceFetcherAgent = sourceFetcherAgent;
        this.summaryAgent = summaryAgent;
        this.newsletterAgent = newsletterAgent;
    }

    public String createForSubject(String subject) {
        log.info("Starting newsletter workflow");
        final AgentOutput sources = this.sourceFetcherAgent.execute(new AgentInput<>(subject));

        final List<NewsClient> newsClients = this.sourceToClient.fromSources(sources.content());
        log.info("Fetching news from {} sources", newsClients.size());

        final List<News> allNews = newsClients.stream()
            .map(NewsClient::fetch)
            .flatMap(List::stream)
            .sorted(Comparator.comparing(News::getPublished).reversed())
            .toList();
        log.info("Fetched total {} news items", allNews.size());

        final AgentOutput summaryResponse = summaryAgent.execute(new AgentInput<>(allNews));
        log.info("News summary created successfully");

        final AgentOutput newsletterResponse = newsletterAgent.execute(new AgentInput<>(summaryResponse));
        log.info("Newsletter created successfully");

        return newsletterResponse.content()
            .replace("```html", "")
            .replace("```", "")
            .trim();
    }

}
