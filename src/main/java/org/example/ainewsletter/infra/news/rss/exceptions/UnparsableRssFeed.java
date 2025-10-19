package org.example.ainewsletter.infra.news.rss.exceptions;

public class UnparsableRssFeed extends RuntimeException {

    public UnparsableRssFeed(Exception exception) {
        super(exception);
    }
}
