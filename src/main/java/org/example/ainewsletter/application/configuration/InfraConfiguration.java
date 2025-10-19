package org.example.ainewsletter.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.NewsFilter;
import org.example.ainewsletter.core.model.news.SourceToClient;
import org.example.ainewsletter.infra.news.rss.RssNewsClient;
import org.example.ainewsletter.infra.news.rss.RssNewsClientFactory;
import org.example.ainewsletter.infra.news.rss.RssNewsParser;
import org.example.ainewsletter.infra.news.rss.SourceToRssClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InfraConfiguration {

    @Bean
    LocalDate limitDate(
        @Value("${news.aggregation.limit-days:7}") final int limitDays
    ) {
        return LocalDate.now().minusDays(limitDays);
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder) {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(10 * 1024 * 1024)) // 10 MB
            .build();

       return webClientBuilder
            .exchangeStrategies(strategies)
            .build();
    }

    @Bean
    RssNewsClientFactory rssNewsClientFactory(
        WebClient webClient,
        LocalDate limitDate
    ) {
        return new RssNewsClientFactory(
            webClient,
            new NewsFilter(limitDate)
        );
    }

    @Bean
    SourceToClient sourceToRssClient(RssNewsClientFactory rssNewsClientFactory){
        return new SourceToRssClient(objectMapper(), rssNewsClientFactory);
    }


}
