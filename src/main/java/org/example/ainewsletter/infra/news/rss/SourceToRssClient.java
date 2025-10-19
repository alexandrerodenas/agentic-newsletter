package org.example.ainewsletter.infra.news.rss;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.SourceToClient;
import org.example.ainewsletter.infra.agent.OllamaWebSearch.SearchResponse;
import org.example.ainewsletter.infra.news.rss.exceptions.CannotConvertSourcesToRssClient;

public final class SourceToRssClient implements SourceToClient {

    private final ObjectMapper objectMapper;
    private final RssNewsClientFactory rssNewsClientFactory;

    public SourceToRssClient(final ObjectMapper objectMapper, final RssNewsClientFactory rssNewsClientFactory) {
        this.objectMapper = objectMapper;
        this.rssNewsClientFactory = rssNewsClientFactory;
    }

    @Override
    public List<NewsClient> fromSources(final String sources) {
        final List<String> urls = Arrays.stream(sources.split("\\R"))
            .filter(line -> !line.isBlank())
            .toList();
        return urls.stream()
            .map(rssNewsClientFactory::create)
            .map(NewsClient.class::cast)
            .toList();
    }
}
