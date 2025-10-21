# AI Newsletter (Agentic Newsletter)

Un projet Spring Boot expérimental qui génère automatiquement une newsletter HTML sur un sujet donné en orchestrant plusieurs agents IA (via Spring AI) :

1. SourceCollectorAgent : recherche des sources pertinentes (web search tool) et identifie des flux RSS associés.
2. PressReviewerAgent : réalise une revue de presse (synthèse + sélection des points saillants).
3NewsletterFormatterAgent : formate le contenu final en HTML prêt à être diffusé.

> Objectif : démontrer un pipeline agentique pour l'agrégation intelligente d'actualités autour d'un thème (ex: IA, cybersécurité, énergie, climat...).

---
## Architecture hexagonale
Le projet suit une séparation claire :

- `application` : couche d'exposition (contrôleurs REST) et configuration Spring.
- `core` : logique métier pure (use case `CreateNewsletter`, modèles `News`, `NewsFilter`, interfaces de services).
- `infra` : implémentations concrètes (agents IA, collecte RSS, formatage, revue de presse).
- `resources/prompts` : templates système et utilisateur pour guider les agents.

### Agents IA
Chaque agent est instancié avec un prompt système + un prompt utilisateur (fichier `.st`) et utilise un `ChatModel` Spring AI (ici un modèle géré via Ollama / API distante).

| Agent | Rôle | Outils | Prompt Variables |
|-------|------|--------|------------------|
| SourceCollector | Génère / affine la liste de sources et flux | web_search (Function Tool) | theme |
| PressReviewer | Synthétise & hiérarchise les articles agrégés | (aucun outil externe) | articles_rss |
| NewsletterFormatter | Mise en page HTML structurée | (aucun outil externe) | content |

### Outil intégré
Un tool nommé `web_search` (déclaré via `FunctionToolCallback`) permet à l'agent SourceCollector d'interroger une API de recherche web (proxy Ollama + RestClient). Le schéma d'entrée est `SearchRequest`.
Il se base sur la fonctionnalité de WebSearch d'Ollama (https://ollama.com/blog/web-search).

---
## Structure du code
Répertoires principaux :
```
src/main/java/org/example/ainewsletter/
  AiNewsletterApplication.java          # Point d'entrée Spring Boot
  application/
    NewsController.java                 # Endpoint REST /newsletter
    configuration/                      # Beans et wiring (Agents, UseCase, Infra, RestClient)
  core/news/                            # Domaine métier (CreateNewsletter, News, NewsFilter, services/*)
  infra/agent/                          # Abstractions & impl. d'agents (BasicAgent, ToolAgent, PromptProvider)
  infra/news/                           # Implémentations NewsCollector, PressReviewer, NewsletterFormatter
  infra/news/rss/                       # Clients & parsing RSS
resources/
  application.yaml                      # Configuration application & Spring AI
  prompts/*.st                          # Templates de prompts système + utilisateur
```

---
## Flux fonctionnel
1. Requête HTTP : `GET /newsletter?subject=<theme>`.
2. Use case `CreateNewsletter` orchestre :
   - SourceCollectorAgent -> thèmes / sources / flux potentiels.
   - NewsCollector -> collecte RSS + filtre par date (paramètre `news.aggregation.limit-days`, défaut 7 jours).
   - PressReviewerAgent -> synthèse / priorisation.
   - NewsletterFormatterAgent -> génération HTML finale.
3. Retour : chaîne HTML (content-type `text/html`).

Diagramme simplifié :
```
           +------------------+
Request -->| NewsController   |
           +--------+---------+
                    |
                    v
           +------------------+
           | CreateNewsletter |
           +--+------+---+----+
              |      |   |
              v      v   v
     SourceCollector  PressReviewer  NewsletterFormatter
           |              |               |
           v              v               v
    RSS Sources -> Collected News -> Reviewed Content -> HTML Output
```

---
## Prérequis
- JDK 25 (déclaré dans le `pom.xml`). Si vous n'avez pas JDK 25, JDK 21 LTS fonctionne généralement mais vous devrez ajuster `maven.compiler.source/target` ou utiliser `--release 21`.
- Maven Wrapper inclus (`mvnw.cmd`).
- Une API Ollama accessible avec un jeton (`OLLAMA_API_KEY`). Le `application.yaml` pointe par défaut sur `https://ollama.com`.
- Accès réseau pour interroger le moteur de recherche et charger les flux RSS.

Optionnel : Docker (si vous souhaitez packager l'application plus tard).

---
## Configuration
Fichier `application.yaml` :
```
spring.ai.ollama.base-url: https://ollama.com
spring.ai.ollama.api-key: ${OLLAMA_API_KEY}
spring.ai.ollama.chat.options.model: gpt-oss:120b
news.aggregation.limit-days: 7
```
Variables d'environnement à définir avant lancement :
```
set OLLAMA_API_KEY=xxxxxxxxxxxxxxxxxxxxxxxx
```
(POWERSHELL) ` $env:OLLAMA_API_KEY="xxxxxxxx" `

Pour changer la fenêtre temporelle :
```
set NEWS_AGGREGATION_LIMIT_DAYS=3  (ou modifier application.yaml)
```

### Changer de modèle
Dans `application.yaml`, ajustez :
```
spring.ai.ollama.chat.options.model: llama3:instruct
```
Assurez-vous que le modèle est supporté par l'offre [Ollama Cloud](https://ollama.com/cloud).

### Base URL locale (si Ollama self-hosté)
```
spring.ai.ollama.base-url: http://localhost:11434
```

---
## Installation & Build
Depuis la racine du projet :

```
# Compilation + tests
mvnw.cmd clean verify

# Ou build simple
mvnw.cmd clean package
```
Le jar sera produit dans `target/ai-newsletter-0.0.1-SNAPSHOT.jar`.

---
## Exécution
```
# Lancer directement via Spring Boot
mvnw.cmd spring-boot:run

# Ou exécuter le jar (après build)
java -jar target/ai-newsletter-0.0.1-SNAPSHOT.jar
```
Assurez-vous que `OLLAMA_API_KEY` est défini dans l'environnement avant exécution.

Démarrage attendu : écoute sur `http://localhost:8080`.

---
## Utilisation de l'API
Endpoint unique pour l'instant :
```
GET /newsletter?subject=<theme>
Accept: text/html
```
Exemple :
```
curl "http://localhost:8080/newsletter?subject=cybersecurite" -H "Accept: text/html"
```
Réponse : HTML contenant la newsletter (titres, synthèse, liens sources). Intégrez ensuite ce HTML dans un email marketing ou un CMS.

---
## Personnalisation
- Prompts : modifier / ajouter des fichiers dans `resources/prompts/*`. Garder les variables (ex: `theme`, `articles_rss`, `content`).
- Fenêtre temporelle : `news.aggregation.limit-days`.
- Log niveau : `logging.level.org.example: DEBUG` peut être réduit à INFO.
- Ajout d'un nouvel outil agent : déclarer un `ToolCallback` dans `AgentConfig` puis l'ajouter à la liste du `ToolAgent`.
- Format HTML : adapter la logique dans `AiNewsletterFormatter`.

---
## Licence
(Sélectionnez une licence adaptée – MIT / Apache-2.0. Ajouter le fichier `LICENSE`.)

---
## Avertissement
Projet expérimental non destiné à la production sans durcissement (sécurité, tests, monitoring, robustesse réseau).

---
Bon hacking ! 🚀
