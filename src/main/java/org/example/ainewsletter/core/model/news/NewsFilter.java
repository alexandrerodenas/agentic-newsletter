package org.example.ainewsletter.core.model.news;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record NewsFilter(
    @NonNull LocalDate limitDate,
    @NonNull List<String> categories
) {

    public static NewsFilter onlyByDate(LocalDate limitDate) {
        return NewsFilter.builder()
            .limitDate(limitDate)
            .categories(List.of())
            .build();
    }
}
