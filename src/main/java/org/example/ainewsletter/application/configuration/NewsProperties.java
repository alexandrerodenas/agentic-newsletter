package org.example.ainewsletter.application.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "news")
public final class NewsProperties {

    private List<Source> sources;

    public record Source (String url, List<String> categories){

        public List<String> getCategories(){
            return categories != null ? categories : List.of();
        }
    }
}