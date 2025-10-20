package org.example.ainewsletter.application.configuration;

import java.util.List;
import java.util.Map;
import org.example.ainewsletter.infra.agent.Agent;
import org.example.ainewsletter.infra.agent.BasicAgent;
import org.example.ainewsletter.infra.agent.PromptProvider;
import org.example.ainewsletter.infra.agent.tools.ToolAgent;
import org.example.ainewsletter.infra.agent.tools.OllamaWebSearch;
import org.example.ainewsletter.infra.agent.tools.OllamaWebSearch.SearchRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;

@Configuration
public class AgentConfig {

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

    @Bean
    @Qualifier("sourceCollectorAgent")
    Agent sourceCollectorAgent(
        @Value("classpath:prompts/source-collector-system.st") Resource systemPrompt,
        @Value("classpath:prompts/source-collector-user.st") Resource userPrompt,
        ChatModel chatModel,
        ToolCallback ollamaWebSearch
    ) {
        final PromptProvider promptProvider = (data) -> new Prompt(
            new SystemPromptTemplate(systemPrompt).createMessage(),
            new PromptTemplate(userPrompt).createMessage(Map.of("theme", data.input()))
        );

        return new ToolAgent(
            "SourceCollector",
            promptProvider,
            chatModel,
            List.of(ollamaWebSearch)
        );
    }

    @Bean
    @Qualifier("pressReviewerAgent")
    Agent pressReviewerAgent(
        @Value("classpath:prompts/press-review-system.st") Resource systemPrompt,
        @Value("classpath:prompts/press-review-user.st") Resource userPrompt,
        ChatModel chatModel
    ) {
        final PromptProvider promptProvider = (data) -> new Prompt(
            new SystemPromptTemplate(systemPrompt).createMessage(),
            new PromptTemplate(userPrompt).createMessage(Map.of("articles_rss", data.input()))
        );

        return new BasicAgent("PressReviewer", promptProvider, chatModel);
    }

    @Bean
    @Qualifier("newsletterFormatterAgent")
    Agent newsletterFormatterAgent(
        @Value("classpath:prompts/newsletter-formatter-system.st") Resource systemPrompt,
        @Value("classpath:prompts/newsletter-formatter-user.st") Resource userPrompt,
        ChatModel chatModel
    ) {
        final PromptProvider promptProvider = (data) -> new Prompt(
            new SystemPromptTemplate(systemPrompt).createMessage(),
            new PromptTemplate(userPrompt).createMessage(Map.of("content", data.input()))
        );
        return new BasicAgent("NewsletterFormatter", promptProvider, chatModel);
    }




}
