package org.example.ainewsletter.application;

import lombok.NonNull;
import org.example.ainewsletter.core.use_cases.FetchNews;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class NewsController {

    private final FetchNews fetchNews;

    public NewsController(@NonNull final FetchNews fetchNews) {
        this.fetchNews = fetchNews;
    }

    @GetMapping(value = "/news", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getNews() {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(fetchNews.aggregateNews());
    }
}
