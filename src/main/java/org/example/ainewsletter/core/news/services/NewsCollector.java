package org.example.ainewsletter.core.news.services;

import java.util.List;
import org.example.ainewsletter.core.news.News;

public interface NewsCollector {
    List<News> collect(String subject);
}
