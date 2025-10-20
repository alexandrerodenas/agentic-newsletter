package org.example.ainewsletter.application.configuration;

import org.example.ainewsletter.core.news.CreateNewsletter;
import org.example.ainewsletter.core.news.services.NewsCollector;
import org.example.ainewsletter.core.news.services.NewsletterFormatter;
import org.example.ainewsletter.core.news.services.PressReviewer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    CreateNewsletter createNewsletter(
        final NewsCollector newsCollector,
        final PressReviewer pressReviewer,
        final NewsletterFormatter newsletterFormatter
    ) {
        return new CreateNewsletter(
            newsCollector,
            pressReviewer,
            newsletterFormatter
        );
    }
}
