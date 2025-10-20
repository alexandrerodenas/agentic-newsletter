package org.example.ainewsletter.core.news.services;

import java.util.List;
import lombok.NonNull;
import org.example.ainewsletter.core.news.News;
import org.example.ainewsletter.core.news.NewsFilter;

public abstract class NewsClient {
    protected final NewsFilter filter;

    protected NewsClient(@NonNull final NewsFilter filter) {
        this.filter = filter;
    }

    public abstract List<News> fetch();
}
