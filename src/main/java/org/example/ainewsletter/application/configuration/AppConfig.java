package org.example.ainewsletter.application.configuration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.use_cases.CreateNewsletter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@Configuration
public class AppConfig {

    @Bean
    CreateNewsletter newsAggregator(
        List<NewsClient> newsClient,
        @Qualifier("summaryAgent") Agent summaryAgent,
        @Qualifier("newsletterAgent") Agent newsletterAgent
    ) {
        return new CreateNewsletter(
            newsClient,
            summaryAgent,
            newsletterAgent
        );
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
}
