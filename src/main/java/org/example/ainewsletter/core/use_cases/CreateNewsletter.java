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
    private final Agent summaryAgent;
    private final Agent newsletterAgent;

    public CreateNewsletter(
        final List<NewsClient> newsClients,
        final Agent summaryAgent,
        final Agent newsletterAgent
    ) {
        this.newsClients = newsClients;
        this.summaryAgent = summaryAgent;
        this.newsletterAgent = newsletterAgent;
        log.info("FetchNews use case initialized with {} news clients", newsClients.size());
        log.info("News Agent initialized: {}", summaryAgent.getClass().getSimpleName());
    }

    public String apply() {
        log.info("Starting news aggregation from {} clients", newsClients.size());

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
