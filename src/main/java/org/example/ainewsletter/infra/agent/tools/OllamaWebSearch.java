package org.example.ainewsletter.infra.agent.tools;

import java.util.List;
import java.util.function.Function;
import org.example.ainewsletter.infra.agent.tools.OllamaWebSearch.SearchRequest;
import org.example.ainewsletter.infra.agent.tools.OllamaWebSearch.SearchResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.client.RestClient;

public final class OllamaWebSearch implements Function<SearchRequest, SearchResponse> {

    private final RestClient restClient;

    public OllamaWebSearch(
        final String baseUrl,
        final RestClient restClient
    ) {
        this.restClient = restClient.mutate()
            .baseUrl(baseUrl)
            .build();
    }

    @Tool(name = "web_search", description = "Search the web for relevant information.")
    @Override
    public SearchResponse apply(@ToolParam(description = "Information to search for")  final SearchRequest searchRequest) {
        return restClient.post()
            .uri("/api/web_search")
            .body(searchRequest)
            .retrieve()
            .toEntity(SearchResponse.class)
            .getBody();
    }

    public record SearchRequest(String query) {

    }

    public record SearchResponse(List<SearchResult> results) {

        public record SearchResult(String title, String url, String content) {

        }
    }
}
