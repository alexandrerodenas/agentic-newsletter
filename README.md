# Agentic Newsletter

An AI-powered newsletter generation system that collects, analyzes, and formats tech news using autonomous agents. The system creates well-structured HTML newsletters with modern styling using TailwindCSS.

## Key Technical Features

- **Multi-Agent Architecture**: Implements a modular agent system for news processing using Spring AI's Chat Models
- **Autonomous Processing Pipeline**:
  - News Collection: Fetches and filters relevant content using configurable news clients
  - Press Review: AI-driven content analysis and categorization
  - Smart Formatting: Automated HTML generation with responsive design
- **LLM Integration**: Leverages [Spring AI](https://spring.io/projects/spring-ai) for natural language processing tasks
- **WebSearch Integration**: Custom Ollama-based web search implementation for content enrichment

## Technology Stack

- **Core Framework**: Spring Boot
- **AI/ML**: Spring AI for LLM integration
- **Frontend Styling**: [TailwindCSS](https://tailwindcss.com/) (CDN version) for modern, responsive design
- **Development Tools**:
  - Lombok for reduced boilerplate
  - SLF4J for logging
  - RestClient for API interactions

## Project Structure

```
.
├── src/main/java/org/example/ainewsletter/
│   ├── application/          # REST controllers and web endpoints
│   ├── core/                 # Core domain logic and interfaces
│   │   ├── news/            # Newsletter generation core components
│   │   └── services/        # Core service interfaces
│   └── infra/               # Infrastructure implementation
│       └── agent/           # AI agent implementation
│           └── tools/       # Agent tools and utilities
└── src/main/resources/
    └── prompts/             # System and user prompts for AI agents
```

Key directories:
- `core/news/`: Contains the central newsletter generation logic
- `infra/agent/`: Houses the AI agent system implementation
- `prompts/`: Stores structured prompts for different agent roles
