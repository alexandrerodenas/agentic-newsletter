package org.example.ainewsletter.core.model.news;

import java.util.List;
import lombok.NonNull;

public abstract class NewsClient {
    protected final NewsFilter filter;

    protected NewsClient(@NonNull final NewsFilter filter) {
        this.filter = filter;
    }

    public abstract List<News> fetch();
}
