package org.example.ainewsletter.application.configuration;

import org.example.ainewsletter.infra.agent.OllamaWebSearch;
import org.example.ainewsletter.infra.agent.OllamaWebSearch.SearchRequest;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ToolConfig {

    @Bean
    OllamaWebSearch ollamaWebSearchClient(
        @Value("${spring.ai.ollama.base-url}") String ollamaBaseUrl,
        RestClient.Builder restClientBuilder
    ) {
        return new OllamaWebSearch(
            ollamaBaseUrl,
            restClientBuilder.build()
        );
    }

    @Bean
    ToolCallback ollamaWebSearch(OllamaWebSearch ollamaWebSearch) {
        return FunctionToolCallback
            .builder("web_search", ollamaWebSearch)
            .description("Run a web search to find relevant information.")
            .inputType(SearchRequest.class)
            .build();
    }
}
