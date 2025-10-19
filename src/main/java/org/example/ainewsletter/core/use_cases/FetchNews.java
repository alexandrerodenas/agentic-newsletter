package org.example.ainewsletter.core.use_cases;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.agent.AgentOutput;
import org.example.ainewsletter.core.model.agent.AgentInput;
import org.example.ainewsletter.core.model.news.News;
import org.example.ainewsletter.core.model.news.NewsClient;

@Slf4j
public final class FetchNews {

    private final List<NewsClient> newsClients;
    private final Agent newsAgent;

    public FetchNews(
        final List<NewsClient> newsClients,
        final Agent newsAgent
    ) {
        this.newsClients = newsClients;
        this.newsAgent = newsAgent;
        log.info("FetchNews use case initialized with {} news clients", newsClients.size());
        log.info("News Agent initialized: {}", newsAgent.getClass().getSimpleName());
    }

    public String aggregateNews() {
        log.info("Starting news aggregation from {} clients", newsClients.size());

        final List<News> allNews = newsClients.stream()
            .map(NewsClient::fetch)
            .flatMap(List::stream)
            .sorted(Comparator.comparing(News::getPublished).reversed())
            .toList();

        log.info("Fetched total {} news items", allNews.size());


        final AgentOutput response = newsAgent.execute(new AgentInput<>(allNews));

        log.info("Aggregation complete with {} items summarized", allNews.size());
        return response.content();
    }

}
