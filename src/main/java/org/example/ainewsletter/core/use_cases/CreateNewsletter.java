package org.example.ainewsletter.core.use_cases;

import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.agent.AgentOutput;
import org.example.ainewsletter.core.model.agent.AgentInput;
import org.example.ainewsletter.core.model.news.News;
import org.example.ainewsletter.core.model.news.NewsClient;

@Slf4j
public final class CreateNewsletter {

    private final List<NewsClient> newsClients;
    private final Agent sourceFetcherAgent;
    private final Agent summaryAgent;
    private final Agent newsletterAgent;

    public CreateNewsletter(
        final List<NewsClient> newsClients,
        final Agent sourceFetcherAgent,
        final Agent summaryAgent,
        final Agent newsletterAgent
    ) {
        this.newsClients = newsClients;
        this.sourceFetcherAgent = sourceFetcherAgent;
        this.summaryAgent = summaryAgent;
        this.newsletterAgent = newsletterAgent;
        log.info("CreateNewsletter use case initialized with {} news clients", newsClients.size());
    }

    public String createForSubject(String subject) {
        log.info("Starting news aggregation from {} clients", newsClients.size());
        final AgentOutput sources = sourceFetcherAgent.execute(new AgentInput<>(subject));

        final List<News> allNews = newsClients.stream()
            .map(NewsClient::fetch)
            .flatMap(List::stream)
            .sorted(Comparator.comparing(News::getPublished).reversed())
            .toList();

        log.info("Fetched total {} news items", allNews.size());

        final AgentOutput summaryResponse = summaryAgent.execute(new AgentInput<>(allNews));
        final AgentOutput newsletterResponse = newsletterAgent.execute(new AgentInput<>(summaryResponse));

        log.info("Aggregation complete with {} items summarized", allNews.size());
        return newsletterResponse.content()
            .replace("```html", "")
            .replace("```", "")
            .trim();
    }

}
