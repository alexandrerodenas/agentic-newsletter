package org.example.ainewsletter.application.configuration;

import java.util.List;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.core.model.news.NewsClient;
import org.example.ainewsletter.core.model.news.SourceToClient;
import org.example.ainewsletter.core.use_cases.CreateNewsletter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    CreateNewsletter createNewsletter(
        SourceToClient sourceToClient,
        @Qualifier("summaryAgent") Agent summaryAgent,
        @Qualifier("newsletterAgent") Agent newsletterAgent,
        @Qualifier("sourceFetcherAgent") Agent sourceFetcherAgent
    ) {
        return new CreateNewsletter(
            sourceToClient,
            sourceFetcherAgent,
            summaryAgent,
            newsletterAgent
        );
    }
}
