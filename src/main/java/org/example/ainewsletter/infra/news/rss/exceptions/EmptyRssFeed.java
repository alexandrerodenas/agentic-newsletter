package org.example.ainewsletter.infra.news.rss.exceptions;

public class EmptyRssFeed extends RuntimeException {

    public EmptyRssFeed() {
        super("No entries found in RSS feed");
    }
}
