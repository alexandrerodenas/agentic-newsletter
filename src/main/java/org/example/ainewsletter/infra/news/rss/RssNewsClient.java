package org.example.ainewsletter.infra.news.rss;

import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.model.news.News;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.NewsFilter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
public final class RssNewsClient extends NewsClient {

    private final WebClient webClient;
    private final RssNewsParser rssNewsParser;
    private final String rssUrl;

    public RssNewsClient(
        final WebClient webClient,
        final RssNewsParser rssNewsParser,
        final String rssUrl,
        final NewsFilter newsFilter
    ) {
        super(newsFilter);
        this.webClient = webClient;
        this.rssNewsParser = rssNewsParser;
        this.rssUrl = rssUrl;
    }

    @Override
    public List<News> fetch() {
        log.debug("Fetching RSS feed from {}", rssUrl);

        try {
            final byte[] rssBytes = this.webClient
                .get()
                .uri(rssUrl)
                .retrieve()
                .onStatus(
                    HttpStatusCode::isError,
                    response -> {
                        log.warn("Non-200 response ({}) when fetching {}", response.statusCode(), rssUrl);
                        return Mono.empty();
                    }
                )
                .bodyToMono(byte[].class)
                .block();

            if (rssBytes == null) {
                log.warn("Empty RSS response from {}", rssUrl);
                return List.of();
            }

            final String rssXml = new String(rssBytes, StandardCharsets.UTF_8);

            final List<News> fetchedNews = this.rssNewsParser.parse(rssXml)
                .stream()
                .filter(news -> news.isValid(this.filter))
                .toList();

            log.debug("Fetched {} news items from {}", fetchedNews.size(), rssUrl);
            return fetchedNews;

        } catch (WebClientResponseException e) {
            log.warn("HTTP error while fetching {}: {}", rssUrl, e.getStatusCode());
            return List.of();
        } catch (IOException | FeedException e) {
            log.warn("Unable to parse RSS feed from {}: {}", rssUrl, e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.warn("Unexpected error fetching {}: {}", rssUrl, e.getMessage());
            return List.of();
        }
    }
}
