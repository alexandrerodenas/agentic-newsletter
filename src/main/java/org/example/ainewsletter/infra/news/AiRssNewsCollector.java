package org.example.ainewsletter.infra.news;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.news.News;
import org.example.ainewsletter.core.news.services.NewsClient;
import org.example.ainewsletter.core.news.services.NewsCollector;
import org.example.ainewsletter.infra.agent.Agent;
import org.example.ainewsletter.infra.agent.AgentInput;
import org.example.ainewsletter.infra.agent.AgentOutput;
import org.example.ainewsletter.infra.news.rss.RssNewsClientFactory;

@Slf4j
public class AiRssNewsCollector implements NewsCollector {

    private final Agent sourceColletorAgent;
    private final RssNewsClientFactory rssNewsClientFactory;

    public AiRssNewsCollector(final Agent sourceColletorAgent, final RssNewsClientFactory rssNewsClientFactory) {
        this.sourceColletorAgent = sourceColletorAgent;
        this.rssNewsClientFactory = rssNewsClientFactory;
    }

    @Override
    public List<News> collect(final String subject) {
        final AgentOutput output = sourceColletorAgent.execute(new AgentInput<>(subject));
        final String sources = output.content();

        final List<String> urls = Arrays.stream(sources.split("\\R"))
            .filter(line -> !line.isBlank())
            .toList();
        log.info("Collected {} news sources", urls.size());

        final List<NewsClient> newsClients = urls.stream()
            .map(rssNewsClientFactory::create)
            .map(NewsClient.class::cast)
            .toList();

        final List<News> news = newsClients.stream()
            .map(NewsClient::fetch)
            .flatMap(List::stream)
            .sorted(Comparator.comparing(News::getPublished).reversed())
            .toList();
        log.info("Fetched total {} news items", news.size());

        return news;
    }
}
