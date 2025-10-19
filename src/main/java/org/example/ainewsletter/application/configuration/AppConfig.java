package org.example.ainewsletter.application.configuration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.use_cases.FetchNews;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@Configuration
public class AppConfig {

    @Bean
    FetchNews newsAggregator(
        List<NewsClient> newsClient,
        Agent newsAgent
    ) {
        return new FetchNews(
            newsClient,
            newsAgent
        );
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
}
