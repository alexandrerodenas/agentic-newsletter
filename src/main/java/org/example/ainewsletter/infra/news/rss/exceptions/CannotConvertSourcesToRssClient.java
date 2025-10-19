package org.example.ainewsletter.infra.news.rss.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CannotConvertSourcesToRssClient extends RuntimeException {

    public CannotConvertSourcesToRssClient(JsonProcessingException exception) {
        super(exception);
    }
}
