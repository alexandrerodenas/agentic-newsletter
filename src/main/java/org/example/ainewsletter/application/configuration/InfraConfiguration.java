package org.example.ainewsletter.application.configuration;

import java.time.LocalDate;
import org.example.ainewsletter.core.news.NewsFilter;
import org.example.ainewsletter.core.news.services.NewsCollector;
import org.example.ainewsletter.core.news.services.NewsletterFormatter;
import org.example.ainewsletter.core.news.services.PressReviewer;
import org.example.ainewsletter.infra.agent.Agent;
import org.example.ainewsletter.infra.news.AiNewsletterFormatter;
import org.example.ainewsletter.infra.news.AiPressReviewer;
import org.example.ainewsletter.infra.news.AiRssNewsCollector;
import org.example.ainewsletter.infra.news.rss.RssNewsClientFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
    NewsCollector newsCollector(
        @Qualifier("sourceCollectorAgent") Agent sourceCollectorAgent,
        RssNewsClientFactory rssNewsClientFactory
    ) {
        return new AiRssNewsCollector(sourceCollectorAgent, rssNewsClientFactory);
    }

    @Bean
    PressReviewer pressReviewer(
        @Qualifier("pressReviewerAgent") Agent pressReviewerAgent
    ) {
        return new AiPressReviewer(pressReviewerAgent);
    }

    @Bean
    NewsletterFormatter newsletterFormatter(
        @Qualifier("newsletterFormatterAgent") Agent newsletterFormatterAgent
    ) {
        return new AiNewsletterFormatter(newsletterFormatterAgent);
    }

}
