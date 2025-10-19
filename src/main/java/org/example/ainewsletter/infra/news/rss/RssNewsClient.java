package org.example.ainewsletter.infra.news.rss;

import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.news.News;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.NewsFilter;
import org.example.ainewsletter.infra.news.rss.exceptions.UnparsableRssFeed;
import org.example.ainewsletter.infra.news.rss.exceptions.EmptyBytesInResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public final class RssNewsClient extends NewsClient {

    private final WebClient webClient;
    private final RssParser rssParser;
    private final String rssUrl;

    public RssNewsClient(
        final WebClient webClient,
        final RssParser rssParser,
        final String rssUrl,
        final NewsFilter newsFilter
    ) {
        super(newsFilter);
        this.webClient = webClient;
        this.rssParser = rssParser;
        this.rssUrl = rssUrl;
    }

    @Override
    public List<News> fetch() {
        log.debug("Fetching RSS feed from {}", rssUrl);
        final byte[] rssBytes = this.webClient
            .get()
            .uri(rssUrl)
            .retrieve()
            .bodyToMono(byte[].class)
            .block();

        // FIXME handle status codes different than 200

        if (rssBytes == null) {
            throw new EmptyBytesInResponse(rssUrl);
        }

        final String rssXml = new String(rssBytes, StandardCharsets.UTF_8);
        try {
            final List<News> fetchedNews = this.rssParser.parse(rssXml)
                .stream()
                .filter(news -> news.isValid(this.filter))
                .toList();
            log.debug("Fetched {} news items from {}", fetchedNews.size(), rssUrl);
            return fetchedNews;
        } catch (IOException | FeedException e) {
            throw new UnparsableRssFeed(e);
        }
    }
}
