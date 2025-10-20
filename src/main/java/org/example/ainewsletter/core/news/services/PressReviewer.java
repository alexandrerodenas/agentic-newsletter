package org.example.ainewsletter.core.news.services;

import java.util.List;
import org.example.ainewsletter.core.news.News;

public interface PressReviewer {
    String review(List<News> news);
}
