package org.example.ainewsletter.application.configuration;

import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.infra.agent.PromptProvider;
import org.example.ainewsletter.infra.agent.SpringAiAgent;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    PromptProvider summaryPromptProvider() {
        final String system = """
                Tu es un assistant spécialisé dans la synthèse de contenus web.
                Lorsque je te fournis un ensemble d'articles (titre, date, description, lien), fournis :
                1) Un titre court résumant le fil (max 8 mots).
                2) Une synthèse en 6-8 lignes maximum (français).
                3) Une liste de 5 points clés (bullet points) — chaque point une phrase courte.
                4) Pour chaque article fourni, une phrase résumant l'article (max 2 phrases par article).
                5) Indique s'il y a des thèmes récurrents ou tendances (2-3 phrases).
                6) Réponds uniquement en texte sous le format Markdown.
                7) Précise bien la date des articles dans les résumés.
                8) Si des informations sont manquantes, signale-le succinctement.
                9) Ne mentionne pas les consignes ou instructions dans ta réponse.
                
                Tu formatteras ta réponse exactement comme suit :
                # Titre du fil d'actualité avec le thème
                
                (pour chaque article)
                ## Titre de l'article ([date de l'article])
                Brève phrase résumant l'article.
                Source : [lien de l'article]
                
                ### Synthèse
                Texte de la synthèse en 6-8 lignes.
                
                """;
        final String user = """
                Voici les derniers articles extraits du flux RSS :
               
                %s

                Produis la synthèse demandée en respectant strictement la structure et la longueur indiquées.
               """;

        return (data) -> new Prompt(
            new SystemMessage(system),
            new UserMessage(user.formatted(data))
        );
    }

    @Bean
    Agent newsAgent(PromptProvider summaryPromptProvider, ChatModel chatModel) {
        return new SpringAiAgent("News", summaryPromptProvider, chatModel);
    }

}
