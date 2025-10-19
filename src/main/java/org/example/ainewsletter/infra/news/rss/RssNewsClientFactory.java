package org.example.ainewsletter.infra.news.rss;

import org.example.ainewsletter.core.model.news.NewsFilter;
import org.springframework.web.reactive.function.client.WebClient;

public final class RssNewsClientFactory {

    private final WebClient webClient;
    private final NewsFilter newsFilter;
    private final RssNewsParser rssNewsParser = new RssNewsParser();

    public RssNewsClientFactory(final WebClient webClient, final NewsFilter newsFilter) {
        this.webClient = webClient;
        this.newsFilter = newsFilter;
    }

    public RssNewsClient create(String sourceUrl) {
        return new RssNewsClient(
            webClient,
            rssNewsParser,
            sourceUrl,
            newsFilter
        );
    }

}
