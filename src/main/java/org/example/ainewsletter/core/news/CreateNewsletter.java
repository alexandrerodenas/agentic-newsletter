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
        log.info("Starting newsletter workflow");
        final List<News> collectedNews = this.newsCollector.collect(subject);
        log.info("Collected {} news items", collectedNews.size());

        final String review = this.pressReviewer.review(collectedNews);
        log.info("Press review completed");

        final String newsletter = this.newsletterFormatter.format(review);
        log.info("Newsletter formatted successfully");

        return newsletter;

/*        final AgentOutput sources = this.sourceFetcherAgent.execute(new AgentInput<>(subject));

        final List<NewsClient> newsClients = this.sourceToClient.fromSources(sources.content());
        log.info("Fetching news from {} sources", newsClients.size());

        final List<News> allNews = newsClients.stream()
            .map(NewsClient::fetch)
            .flatMap(List::stream)
            .sorted(Comparator.comparing(News::getPublished).reversed())
            .toList();
        log.info("Fetched total {} news items", allNews.size());

        final AgentOutput summaryResponse = summaryAgent.execute(new AgentInput<>(allNews));
        log.info("News summary created successfully");

        final AgentOutput newsletterResponse = newsletterAgent.execute(new AgentInput<>(summaryResponse));
        log.info("Newsletter created successfully");

        return newsletterResponse.content()
            .replace("```html", "")
            .replace("```", "")
            .trim();*/
    }

}
