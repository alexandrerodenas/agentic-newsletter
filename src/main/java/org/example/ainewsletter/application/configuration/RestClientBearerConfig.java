package org.example.ainewsletter.application.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientBearerConfig {

    @Bean
    public RestClient.Builder restClientBuilder(
        @Value("${spring.ai.ollama.api-key}") String token
    ) {
        return RestClient.builder()
            .defaultHeader("Authorization", "Bearer " + token);
    }

}
