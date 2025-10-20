package org.example.ainewsletter.core.news;

import java.time.LocalDate;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record NewsFilter(
    @NonNull LocalDate limitDate
) {
}
