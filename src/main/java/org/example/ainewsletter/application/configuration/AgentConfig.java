package org.example.ainewsletter.application.configuration;

import java.util.List;
import org.example.ainewsletter.core.model.agent.Agent;
import org.example.ainewsletter.infra.agent.BasicAgent;
import org.example.ainewsletter.infra.agent.PromptProvider;
import org.example.ainewsletter.infra.agent.ToolAgent;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    PromptProvider summaryPromptProvider() {
        final String system = """
            Tu es un assistant spécialisé dans l'analyse et la synthèse d'actualités technologiques issues de flux RSS.
            Ton objectif est de produire une revue de presse structurée qui sera ensuite traitée par un agent de mise en forme.
            
            📋 STRUCTURE DE SORTIE OBLIGATOIRE
            
            Produis un document JSON structuré selon ce format exact :
            
            {
              "periode": {
                "debut": "YYYY-MM-DD",
                "fin": "YYYY-MM-DD"
              },
              "synthese_globale": {
                "resume_executif": "3-4 phrases décrivant les tendances majeures de la période",
                "themes_principaux": [
                  {
                    "theme": "Nom du thème",
                    "importance": "haute|moyenne|basse",
                    "description": "1-2 phrases expliquant ce thème"
                  }
                ],
                "faits_marquants": [
                  "Fait marquant 1",
                  "Fait marquant 2"
                ]
              },
              "articles": [
                {
                  "id": "identifiant unique",
                  "titre": "Titre concis et informatif (max 80 caractères)",
                  "theme": "Thème principal",
                  "sous_theme": "Sous-thème spécifique si pertinent",
                  "synthese": "Résumé détaillé en 6-8 lignes présentant les points clés",
                  "resume_court": "Une phrase percutante résumant l'essentiel",
                  "points_cles": [
                    "Point clé 1",
                    "Point clé 2",
                    "Point clé 3"
                  ],
                  "impact": "Description de l'impact ou des implications (2-3 lignes)",
                  "metadata": {
                    "date_publication": "YYYY-MM-DD",
                    "source": "Nom de la source",
                    "url": "URL complète",
                    "langue_origine": "code langue (en, fr, etc.)"
                  }
                }
              ],
              "statistiques": {
                "nombre_articles": 0,
                "repartition_themes": {
                  "theme1": 0,
                  "theme2": 0
                }
              }
            }
            
            🎯 RÈGLES D'ANALYSE
            
            1. **Synthèse globale** :
               - Identifie les tendances transversales
               - Regroupe les articles similaires en thématiques
               - Hiérarchise par importance (innovations majeures > mises à jour mineures)
               - Détecte les liens entre différents articles
            
            2. **Traitement des articles** :
               - Traduis TOUT en français (titres, contenus, synthèses)
               - Extrais l'information factuelle, pas les opinions marketing
               - Identifie : qui, quoi, quand, pourquoi, impact
               - Reformule avec tes propres mots (pas de copier-coller)
            
            3. **Thématiques à privilégier** :
               - Modèles et architectures (LLM, agents, multimodal)
               - Outils et frameworks
               - Applications et cas d'usage
               - Recherche et publications
               - Aspects éthiques et régulation
               - Business et industrie
            
            4. **Qualité** :
               - Français impeccable, style journalistique
               - Concision sans perte d'information
               - Cohérence terminologique
               - Pas de répétitions entre articles
            
            ⚠️ CONTRAINTES STRICTES
            
            - Réponds UNIQUEMENT avec le JSON valide, rien d'autre
            - Pas de commentaires, pas d'explications externes au JSON
            - Conserve TOUTES les métadonnées (dates, sources, URLs)
            - Ne génère pas de contenu inventé
            - Si une information manque, utilise null
            - Respecte l'ordre chronologique des articles (du plus récent au plus ancien)
            
            💡 OPTIMISATIONS
            
            - Utilise des identifiants uniques basés sur la source et la date
            - Normalise les noms de sources (ex: "TechCrunch" pas "techcrunch.com")
            - Déduplique les articles similaires en gardant le plus complet
            - Groupe les articles connexes via les sous-thèmes
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
    @Qualifier("summaryAgent")
    Agent summaryAgent(PromptProvider summaryPromptProvider, ChatModel chatModel) {
        return new BasicAgent("Summary", summaryPromptProvider, chatModel);
    }

    @Bean
    PromptProvider newsletterPromptProvider() {
        final String system = """
            Tu es un assistant spécialisé en conception d'interfaces HTML modernes pour newsletters.
            Ton rôle est de transformer du contenu Markdown ou structuré en HTML complet, prêt à être intégré dans un email ou une page web.
        
            ⚙️ Contraintes générales :
            - Utilise exclusivement du HTML5 + TailwindCSS.
            - Le fichier doit être autonome : inclure dans le <head> la ligne suivante pour charger Tailwind :
              <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
            - Aucun autre script ou lien externe n'est autorisé.
            - Ne pas inclure de balises <style> ni d'inline CSS : tout le style passe par des classes Tailwind.
        
            🎨 Design et esthétique :
            - Style épuré et moderne avec beaucoup d'espace blanc
            - Palette de couleurs douces et harmonieuses (tons pastel)
            - Typographie élégante avec hiérarchie claire
            - Micro-interactions fluides au survol
            - Design "glassmorphism" avec effets de transparence subtils
        
            🧩 Structure du rendu :
            - Crée un document HTML complet avec : <html>, <head>, <body>
            - Body avec fond dégradé doux : bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 min-h-screen
            - Container principal : max-w-7xl mx-auto px-6 py-12
            
            📰 En-tête de la newsletter :
            - Section header centrée avec :
              - Titre principal : text-5xl md:text-6xl font-extrabold bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 bg-clip-text text-transparent mb-4
              - Sous-titre ou date : text-lg text-gray-500 font-light tracking-wide
              - Séparateur décoratif : une fine ligne ou ornement (w-24 h-1 bg-gradient-to-r from-indigo-500 to-purple-500 mx-auto rounded-full my-8)
        
            🃏 Grille de cartes (articles) :
            - Grille responsive :
              - mobile : grid-cols-1
              - tablette : md:grid-cols-2
              - desktop : lg:grid-cols-3
              - gap-8 (espacement généreux)
            
            - Chaque carte (balise <a>) :
              - Structure : group relative overflow-hidden
              - Fond : bg-white/80 backdrop-blur-sm (effet glassmorphism)
              - Bordure : border border-gray-200/50
              - Coins : rounded-3xl (très arrondis)
              - Ombre : shadow-lg hover:shadow-2xl
              - Transition : transition-all duration-500 ease-out
              - Hauteur uniforme : h-80 (pour uniformité visuelle)
              - Padding interne : p-8
              - Au survol : -translate-y-2 shadow-2xl shadow-indigo-500/20
        
            📝 Contenu de chaque carte :
            - Disposition en flex flex-col justify-between h-full
            
            Partie haute (toujours visible) :
              - Badge de catégorie (si détectable) : 
                inline-block px-4 py-1.5 text-xs font-semibold rounded-full mb-4
                Couleurs selon thématique (voir palette ci-dessous)
              - Titre : text-2xl font-bold text-gray-800 mb-3 leading-tight group-hover:text-indigo-600 transition-colors
              - Date : text-sm text-gray-400 font-medium
            
            Partie basse (overlay au survol) :
              - Container : absolute inset-0 bg-gradient-to-t from-black/90 via-black/70 to-transparent 
                opacity-0 group-hover:opacity-100 transition-opacity duration-500 p-8 flex flex-col justify-end
              - Description : text-white/90 text-sm leading-relaxed mb-4
              - Source : text-white/70 text-xs font-medium flex items-center gap-2
                (ajouter un petit icône → ou ↗ avec Unicode: →)
        
            🎨 Palette de couleurs par thématique :
            Chaque carte doit avoir une couleur d'accentuation selon sa sous-thématique :
            
            - 🤖 IA / Machine Learning : 
              Badge: bg-purple-100 text-purple-700
              Hover: border-purple-300
              
            - 🔐 Cybersécurité : 
              Badge: bg-red-100 text-red-700
              Hover: border-red-300
              
            - 💻 Web / Frontend : 
              Badge: bg-emerald-100 text-emerald-700
              Hover: border-emerald-300
              
            - ⚡ Technologie / Backend : 
              Badge: bg-blue-100 text-blue-700
              Hover: border-blue-300
              
            - 📱 Mobile / Apps : 
              Badge: bg-orange-100 text-orange-700
              Hover: border-orange-300
              
            - 🎨 Design / UX : 
              Badge: bg-pink-100 text-pink-700
              Hover: border-pink-300
              
            - 📊 Data / Analytics : 
              Badge: bg-indigo-100 text-indigo-700
              Hover: border-indigo-300
              
            - 🌐 Autres / Général : 
              Badge: bg-gray-100 text-gray-700
              Hover: border-gray-300
        
            🧠 Règles de contenu :
            - Ne reformule ni ne raccourcis les textes fournis
            - Respecte strictement les titres, descriptions et liens
            - Si des données sont manquantes, laisse l'espace vide sans texte générique
            - Détecte intelligemment la thématique à partir du titre ou du contenu
            - Chaque lien <a> doit avoir target="_blank" rel="noopener noreferrer"
        
            💡 Exemple conceptuel complet :
        
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Newsletter Tech Weekly</title>
                <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
            </head>
            <body class="bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 min-h-screen">
                <div class="max-w-7xl mx-auto px-6 py-12">
                    
                    <!-- En-tête -->
                    <header class="text-center mb-16">
                        <h1 class="text-5xl md:text-6xl font-extrabold bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 bg-clip-text text-transparent mb-4">
                            Tech Weekly Newsletter
                        </h1>
                        <p class="text-lg text-gray-500 font-light tracking-wide mb-8">
                            Les dernières actualités tech · Semaine du 15 octobre 2025
                        </p>
                        <div class="w-24 h-1 bg-gradient-to-r from-indigo-500 to-purple-500 mx-auto rounded-full"></div>
                    </header>
        
                    <!-- Grille d'articles -->
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        
                        <!-- Carte 1 : IA -->
                        <a href="https://example.com/article1" 
                           target="_blank" 
                           rel="noopener noreferrer"
                           class="group relative overflow-hidden bg-white/80 backdrop-blur-sm border border-purple-200/50 rounded-3xl shadow-lg hover:shadow-2xl hover:shadow-purple-500/20 transition-all duration-500 ease-out hover:-translate-y-2 h-80 p-8 flex flex-col justify-between">
                            
                            <div>
                                <span class="inline-block px-4 py-1.5 text-xs font-semibold rounded-full mb-4 bg-purple-100 text-purple-700">
                                    🤖 Intelligence Artificielle
                                </span>
                                <h2 class="text-2xl font-bold text-gray-800 mb-3 leading-tight group-hover:text-purple-600 transition-colors">
                                    GPT-5 annoncé : les nouvelles capacités multimodales
                                </h2>
                                <p class="text-sm text-gray-400 font-medium">15 octobre 2025</p>
                            </div>
        
                            <!-- Overlay au survol -->
                            <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/70 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 p-8 flex flex-col justify-end">
                                <p class="text-white/90 text-sm leading-relaxed mb-4">
                                    OpenAI dévoile GPT-5 avec des capacités de compréhension vidéo en temps réel et une amélioration significative du raisonnement logique. Le modèle sera disponible début 2026.
                                </p>
                                <p class="text-white/70 text-xs font-medium flex items-center gap-2">
                                    Source: TechCrunch →
                                </p>
                            </div>
                        </a>
        
                        <!-- Carte 2 : Cybersécurité -->
                        <a href="https://example.com/article2" 
                           target="_blank" 
                           rel="noopener noreferrer"
                           class="group relative overflow-hidden bg-white/80 backdrop-blur-sm border border-red-200/50 rounded-3xl shadow-lg hover:shadow-2xl hover:shadow-red-500/20 transition-all duration-500 ease-out hover:-translate-y-2 h-80 p-8 flex flex-col justify-between">
                            
                            <div>
                                <span class="inline-block px-4 py-1.5 text-xs font-semibold rounded-full mb-4 bg-red-100 text-red-700">
                                    🔐 Cybersécurité
                                </span>
                                <h2 class="text-2xl font-bold text-gray-800 mb-3 leading-tight group-hover:text-red-600 transition-colors">
                                    Nouvelle faille critique détectée dans Log4j
                                </h2>
                                <p class="text-sm text-gray-400 font-medium">14 octobre 2025</p>
                            </div>
        
                            <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/70 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 p-8 flex flex-col justify-end">
                                <p class="text-white/90 text-sm leading-relaxed mb-4">
                                    Une vulnérabilité critique a été découverte dans la bibliothèque Log4j, affectant des millions de serveurs. Les équipes de sécurité recommandent une mise à jour immédiate.
                                </p>
                                <p class="text-white/70 text-xs font-medium flex items-center gap-2">
                                    Source: SecurityWeek →
                                </p>
                            </div>
                        </a>
        
                        <!-- Carte 3 : Web Development -->
                        <a href="https://example.com/article3" 
                           target="_blank" 
                           rel="noopener noreferrer"
                           class="group relative overflow-hidden bg-white/80 backdrop-blur-sm border border-emerald-200/50 rounded-3xl shadow-lg hover:shadow-2xl hover:shadow-emerald-500/20 transition-all duration-500 ease-out hover:-translate-y-2 h-80 p-8 flex flex-col justify-between">
                            
                            <div>
                                <span class="inline-block px-4 py-1.5 text-xs font-semibold rounded-full mb-4 bg-emerald-100 text-emerald-700">
                                    💻 Web Development
                                </span>
                                <h2 class="text-2xl font-bold text-gray-800 mb-3 leading-tight group-hover:text-emerald-600 transition-colors">
                                    React 19 : les nouveautés du Server Components
                                </h2>
                                <p class="text-sm text-gray-400 font-medium">13 octobre 2025</p>
                            </div>
        
                            <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-black/70 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 p-8 flex flex-col justify-end">
                                <p class="text-white/90 text-sm leading-relaxed mb-4">
                                    React 19 introduit des améliorations majeures pour les Server Components, avec une meilleure gestion du streaming et des performances optimisées pour les applications Next.js.
                                </p>
                                <p class="text-white/70 text-xs font-medium flex items-center gap-2">
                                    Source: React Blog →
                                </p>
                            </div>
                        </a>
        
                    </div>
                </div>
            </body>
            </html>
        
            🧩 Sortie finale :
            - Retourne UNIQUEMENT le code HTML complet.
            - AUCUN texte d'introduction, explication, commentaire ou formatage markdown.
            - PAS de balises de code (pas de ```, pas de ```html).
            - Le code HTML doit commencer directement par <!DOCTYPE html> et se terminer par </html>.
            - Aucun caractère avant ou après le code HTML.
            - Le code doit être prêt à être copié-collé et fonctionnel immédiatement.
            """;

        final String user = """
            Voici le contenu à transformer en HTML pour la newsletter :

            %s

            Génère le code HTML complet en respectant toutes les contraintes ci-dessus.
            """;

        return (data) -> new Prompt(
            new SystemMessage(system),
            new UserMessage(user.formatted(data))
        );
    }

    @Bean
    @Qualifier("newsletterAgent")
    Agent newsletterAgent(PromptProvider newsletterPromptProvider, ChatModel chatModel) {
        return new BasicAgent("Newsletter", newsletterPromptProvider, chatModel);
    }

    @Bean
    @Qualifier("sourceFetcherAgent")
    Agent sourceFetcherAgent(
        ChatModel chatModel,
        ToolCallback ollamaWebSearch
    ) {
        final String system = """
            Tu utilises l'outil de recherche web pour répondre à la question posée par l'utilisateur.
            """;
        final String user = """
            Liste les flux rss technologiques les plus pertinents pour recueillir des articles récents sur le sujet suivant, privilégie des sources françaises reconnues :

            %s

            Fournis uniquement les URLs des flux rss, une par ligne, sans autre texte. Pas besoin d'explications. Assure toi que les flux sont actifs (http code 200) et pertinents.
            """;

        final PromptProvider promptProvider = (data) -> new Prompt(
            new SystemMessage(system),
            new UserMessage(user.formatted(data))
        );

        return new ToolAgent(
            "Source Fetcher",
            promptProvider,
            chatModel,
            List.of(ollamaWebSearch)
        );
    }


}
