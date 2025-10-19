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
}
