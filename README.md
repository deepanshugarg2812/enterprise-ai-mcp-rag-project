# Enterprise AI Operations Assistant

An enterprise-grade AI assistant built with **Java, Spring Boot, Spring AI, Ollama, PostgreSQL, pgvector, and MCP (Model Context Protocol)**.

The project is developed incrementally across six phases, starting from a traditional enterprise order-processing backend and evolving into an AI-powered operations assistant capable of:

* Retrieving enterprise data through MCP tools
* Using enterprise knowledge through RAG
* Calling multiple tools based on a user's request
* Maintaining conversation context
* Orchestrating multi-step tool calls
* Combining structured enterprise data with unstructured knowledge
* Generating grounded responses using a local LLM

The project is intentionally split into multiple services/repositories so that each architectural component can be developed and understood independently.

---

## Architecture

```text
                         ┌──────────────────────┐
                         │        User/UI        │
                         └──────────┬───────────┘
                                    │
                                    ▼
                     ┌──────────────────────────┐
                     │   Enterprise AI Backend  │
                     │                          │
                     │  Spring AI + Agent Loop  │
                     └──────────┬───────┬───────┘
                                │       │
                    MCP Tools   │       │   RAG
                                │       │
                                ▼       ▼
                    ┌──────────────┐  ┌──────────────┐
                    │ MCP Server   │  │ RAG Service  │
                    │              │  │              │
                    │ Enterprise   │  │ Embeddings   │
                    │ Tools        │  │ Retrieval    │
                    └──────┬───────┘  └──────┬───────┘
                           │                 │
                           ▼                 ▼
                    ┌──────────────┐  ┌──────────────┐
                    │ Order/Domain │  │ PostgreSQL   │
                    │ Backend      │  │ + pgvector   │
                    └──────┬───────┘  └──────────────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ PostgreSQL   │
                    │              │
                    │ Orders       │
                    │ OrderSets    │
                    │ Workflows    │
                    └──────────────┘

                         ┌──────────────┐
                         │    Ollama    │
                         │ Local LLM    │
                         └──────▲───────┘
                                │
                         Spring AI
```
