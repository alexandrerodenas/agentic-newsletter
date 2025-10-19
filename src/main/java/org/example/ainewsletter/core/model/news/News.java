package org.example.ainewsletter.core.model.news;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import org.jsoup.parser.Parser;

public final class News {

    private final String title;
    @Getter
    private final LocalDate published;
    private final String description;
    private final String link;
    private final List<String> categories;

    public News(
        @NonNull String title,
        @NonNull LocalDate published,
        @NonNull String description,
        @NonNull String link,
        @NonNull List<String> categories
    ) {
        this.title = title;
        this.published = published;
        this.description = description;
        this.link = link;
        this.categories = categories;
    }

    public static News fromSyndEntry(@NonNull final SyndEntry syndEntry) {
        final Optional<SyndContent> descriptionContent = Optional.ofNullable(syndEntry.getDescription());
        final Optional<SyndContent> firstContent = syndEntry.getContents().stream().findFirst();
        return new News(
            syndEntry.getTitle(),
            syndEntry.getPublishedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
            descriptionContent.or(() -> firstContent)
                .map(SyndContent::getValue)
                .map(desc -> Parser.unescapeEntities(desc, false))
                .orElse(""),
            syndEntry.getLink(),
            syndEntry.getCategories().stream().map(SyndCategory::getName).toList()
        );
    }

    public boolean isValid(NewsFilter filter) {
        if(this.published.isBefore(filter.limitDate())){
            return false;
        }

        if (filter.categories().isEmpty()) {
            return true;
        }

        for(String category : this.categories){
            for(String categoryFilter : filter.categories()){
                if(category.equalsIgnoreCase(categoryFilter)){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString(){
        return String.format("""
                    Titre: %s
                    Date: %s
                    Description: %s
                    Lien: %s
                    """,
            this.title,
            this.published,
            this.description,
            this.link
        );
    }
}
