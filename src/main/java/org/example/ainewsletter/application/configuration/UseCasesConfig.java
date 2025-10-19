package org.example.ainewsletter.application.configuration;

import java.util.List;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.use_cases.CreateNewsletter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    CreateNewsletter createNewsletter(
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
}
