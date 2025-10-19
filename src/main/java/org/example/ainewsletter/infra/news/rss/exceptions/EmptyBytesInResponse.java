package org.example.ainewsletter.infra.news.rss.exceptions;

public class EmptyBytesInResponse extends RuntimeException {

    public EmptyBytesInResponse(String rssUrl) {
        super("Empty bytes in response : " + rssUrl);
    }
}
