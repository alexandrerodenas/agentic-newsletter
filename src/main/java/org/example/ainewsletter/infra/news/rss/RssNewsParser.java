package org.example.ainewsletter.infra.news.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.ainewsletter.core.model.news.News;

public final class RssNewsParser {

    public List<News> parse(final String rssXml) throws IOException, FeedException {
        final XmlReader xmlReader = new XmlReader(
            new ByteArrayInputStream(rssXml.getBytes(StandardCharsets.UTF_8))
        );
        final SyndFeed feed = new SyndFeedInput().build(xmlReader);

        return feed.getEntries()
            .stream()
            .map(News::fromSyndEntry)
            .toList();
    }
}
