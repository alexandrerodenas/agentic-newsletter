package org.example.ainewsletter.application.configuration;

import java.time.LocalDate;
import java.util.List;
import org.example.ainewsletter.core.model.news.NewsFilter;
import org.example.ainewsletter.infra.news.rss.RssNewsClient;
import org.example.ainewsletter.infra.news.rss.RssParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    List<RssNewsClient> rssNewsClients(
        WebClient.Builder webClientBuilder,
        LocalDate limitDate,
        NewsProperties newsProperties
    ) {
        return newsProperties.getSources().stream()
            .map(property -> new RssNewsClient(
                webClientBuilder.build(),
                new RssParser(),
                property.url(),
                new NewsFilter(limitDate, property.getCategories())
            ))
            .toList();
    }


}
