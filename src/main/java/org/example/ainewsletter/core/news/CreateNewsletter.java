package org.example.ainewsletter.core.news;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.ainewsletter.core.news.services.NewsCollector;
import org.example.ainewsletter.core.news.services.NewsletterFormatter;
import org.example.ainewsletter.core.news.services.PressReviewer;

@Slf4j
public final class CreateNewsletter {

    private final NewsCollector newsCollector;
    private final PressReviewer pressReviewer;
    private final NewsletterFormatter newsletterFormatter;

    public CreateNewsletter(
        final NewsCollector newsCollector,
        final PressReviewer pressReviewer,
        final NewsletterFormatter newsletterFormatter
    ) {
        this.newsCollector = newsCollector;
        this.pressReviewer = pressReviewer;
        this.newsletterFormatter = newsletterFormatter;
    }

    public String createForSubject(String subject) {
        log.info("Starting newsletter workflow for subject: {}", subject);
        final List<News> collectedNews = this.newsCollector.collect(subject);
        log.info("Collected {} news items", collectedNews.size());

        final String review = this.pressReviewer.review(collectedNews);
        log.info("Press review completed");

        final String newsletter = this.newsletterFormatter.format(review);
        log.info("Newsletter formatted successfully");

        return newsletter;
    }

}
