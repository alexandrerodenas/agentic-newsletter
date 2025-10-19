package org.example.ainewsletter.core.model.news;

import java.util.List;

public interface SourceToClient {
    List<NewsClient> fromSources(String sources);
}
