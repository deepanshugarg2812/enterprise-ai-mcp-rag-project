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

---

# Project Structure

The parent repository contains the implementations developed across the six phases:

```text
enterprise-ai/
│
├── phase-1-order-service/
│
├── phase-2-mcp-server/
│
├── phase-3-rag-service/
│
├── phase-4-<component>/
│
├── enterprise-ai-backend/
│
└── phase-6-<component>/
```

> The individual services are maintained as separate projects but are combined under this parent repository for the complete system.

---

# Phase 1 — Enterprise Domain & Order Service

The first phase establishes the traditional enterprise backend that the AI system will eventually interact with.

The focus is deliberately **non-AI**.

## Goals

* Build the enterprise domain model
* Store enterprise data in PostgreSQL
* Implement order-related workflows
* Expose APIs for retrieving and modifying business data
* Establish the business layer that later becomes accessible through MCP

## Main Domain

The service models enterprise concepts such as:

* Orders
* OrderSets
* Order-related workflows
* Business operations

The implementation provides APIs that represent the underlying enterprise system rather than exposing database access directly to the AI.

## Example Workflow

```text
Client
  │
  ▼
REST API
  │
  ▼
Service Layer
  │
  ▼
Repository
  │
  ▼
PostgreSQL
```

This separation is important because the AI should eventually interact with **business operations**, not directly with the database.

---

# Phase 2 — MCP Server

The second phase introduces **Model Context Protocol (MCP)**.

The goal is to expose enterprise capabilities as tools that an AI system can discover and invoke.

## Why MCP?

Instead of tightly coupling the AI backend to individual REST APIs:

```text
AI Backend
   │
   ├── getOrder()
   ├── getOrderStatus()
   ├── getOrderSet()
   └── updateOrder()
```

the capabilities are exposed through MCP:

```text
AI Backend
     │
     │ MCP
     ▼
MCP Server
     │
     ├── getOrder
     ├── getOrderStatus
     ├── getOrderSet
     └── other enterprise tools
```

The MCP server becomes the standardized tool boundary between the AI system and enterprise operations.

## Tool Design

Tools provide:

* Tool name
* Description
* Input parameters
* Input validation
* Business operation
* Structured result

Tool descriptions are important because the LLM uses them to determine:

* What the tool does
* When it should be used
* What parameters it requires
* What information it can return

## Example

```text
User:
"What is the status of order 123?"

        │
        ▼

       LLM
        │
        │ decides tool is required
        ▼
get_order_status(orderId=123)
        │
        ▼
   MCP Server
        │
        ▼
 Order Service
        │
        ▼
 PostgreSQL
        │
        ▼
 Tool Result
```

The MCP server does not decide the next AI action.

It provides the standardized mechanism through which the AI client can discover and invoke tools.

---

# Phase 3 — RAG Knowledge Base

The third phase adds **Retrieval-Augmented Generation (RAG)**.

MCP is used for **live enterprise data**, while RAG is used for **enterprise knowledge**.

### MCP

Useful for:

```text
Orders
Customers
OrderSets
Current status
Live operational data
```

### RAG

Useful for:

```text
Policies
Documentation
Business rules
Procedures
Enterprise knowledge
```

This separation prevents the LLM from treating static documentation and live transactional data as the same type of information.

---

## RAG Architecture

```text
Documents
    │
    ▼
Document Processing
    │
    ▼
Chunking
    │
    ▼
Embedding Model
    │
    ▼
PostgreSQL + pgvector
```

At query time:

```text
User Question
      │
      ▼
Query Embedding
      │
      ▼
Vector Search
      │
      ▼
Relevant Chunks
      │
      ▼
LLM
      │
      ▼
Grounded Answer
```

## Technology

* Spring Boot
* Spring AI
* PostgreSQL
* pgvector
* Embeddings
* Vector similarity search

The RAG service supports retrieval of relevant enterprise documentation rather than asking the LLM to rely solely on its pretrained knowledge.

---

# Phase 4 — AI / Spring AI Integration

The fourth phase connects the AI layer with the previously developed components.

The project uses:

* Spring AI
* Ollama
* Local LLM
* Tool calling
* MCP tools
* RAG tools
* Conversation context

The basic interaction becomes:

```text
User
 │
 ▼
Spring AI
 │
 ▼
Ollama LLM
 │
 ├── MCP tools
 │
 └── RAG tool
```

The system prompt establishes the assistant's role and grounding rules.

Example:

```text
You are an enterprise operations assistant.

You can use tools to retrieve enterprise data.

Rules:
- Do not invent business data.
- Use MCP tools for live business/customer/order data.
- Use the knowledge-base tool for enterprise policies,
  documentation, and business rules.
- When tool results are available, base your answer
  on those results.
- If the available tools do not provide enough
  information, clearly say so.
```

---

# Phase 5 — Enterprise AI Backend

Phase 5 introduces the dedicated AI backend responsible for orchestrating the AI interaction.

This service combines:

* Spring AI
* Ollama
* MCP client
* MCP tools
* RAG client/tool
* Conversation handling

The backend becomes the central AI orchestration layer.

```text
                    User
                     │
                     ▼
            Enterprise AI Backend
                     │
             ┌───────┴────────┐
             ▼                ▼
        MCP Client         RAG Client
             │                │
             ▼                ▼
        MCP Server       RAG Service
             │                │
             ▼                ▼
       Enterprise DB     pgvector
```

---

# Phase 6 — Agentic Tool Orchestration

The final phase turns the AI backend into an agent-style system.

The important concept is that the LLM is not limited to a single tool call.

A user request can require multiple steps.

For example:

```text
User:
"Find order 123 and tell me whether its current status
violates our cancellation policy."
```

The system may need to:

```text
1. Retrieve order 123
        ↓
2. Retrieve cancellation policy
        ↓
3. Compare order information with policy
        ↓
4. Generate final answer
```

Conceptually:

```text
                 ┌───────────────┐
                 │ User Request  │
                 └───────┬───────┘
                         │
                         ▼
                    ┌─────────┐
                    │   LLM   │
                    └────┬────┘
                         │
                  Tool required?
                    /          \
                  YES           NO
                   │             │
                   ▼             ▼
              Execute Tool   Final Answer
                   │
                   ▼
              Tool Result
                   │
                   ▼
                 LLM
                   │
            Another tool?
              /        \
            YES         NO
             │           │
             ▼           ▼
        Execute Tool   Answer
```

## Important Architectural Detail

The **agent loop belongs to the AI orchestration layer**, not MCP.

MCP provides the protocol for discovering and invoking tools.

The AI backend decides how to repeatedly interact with the LLM and tools.

Conceptually, the agent behaves like:

```java
while (true) {

    LLMResponse response = callLLM(messages, tools);

    if (!response.hasToolCalls()) {
        return response;
    }

    for (ToolCall toolCall : response.getToolCalls()) {
        ToolResult result = executeTool(toolCall);
        messages.add(result);
    }
}
```

When using Spring AI's higher-level tool calling APIs, this orchestration can be managed by the framework rather than being explicitly implemented in application code.

---

# Tool Sources

The assistant has two primary sources of information.

## 1. MCP — Live Enterprise Data

```text
MCP
 │
 ├── Order information
 ├── Order status
 ├── OrderSets
 └── Other enterprise operations
```

MCP tools should be used whenever the answer depends on current business data.

## 2. RAG — Enterprise Knowledge

```text
RAG
 │
 ├── Policies
 ├── Documentation
 ├── Business rules
 └── Procedures
```

RAG should be used when the answer depends on enterprise documentation or policies.

---

# Example End-to-End Request

Consider:

```text
"Can order 123 still be cancelled according to our policy?"
```

The agent can reason through the available tools:

```text
User
 │
 ▼
AI Backend
 │
 ▼
LLM
 │
 ├── get_order(123)
 │
 ▼
MCP Server
 │
 ▼
Order Service
 │
 ▼
Order information
 │
 ▼
LLM
 │
 ├── search_knowledge_base("cancellation policy")
 │
 ▼
RAG Service
 │
 ▼
Relevant policy chunks
 │
 ▼
LLM
 │
 ▼
Final grounded answer
```

The final answer is based on retrieved enterprise information rather than invented business data.

---

# Technology Stack

| Component       | Technology                                 |
| --------------- | ------------------------------------------ |
| Language        | Java                                       |
| Backend         | Spring Boot                                |
| AI Framework    | Spring AI                                  |
| LLM             | Ollama                                     |
| Protocol        | Model Context Protocol (MCP)               |
| Database        | PostgreSQL                                 |
| Vector Database | PostgreSQL + pgvector                      |
| API             | REST                                       |
| Build Tool      | Maven                                      |
| Architecture    | Multi-service                              |
| AI Pattern      | Tool Calling + RAG + Agentic Orchestration |

---

# Core Design Principles

## 1. Separate AI from Business Logic

The LLM does not directly access the database.

```text
LLM
 │
 ▼
MCP
 │
 ▼
Business APIs
 │
 ▼
Database
```

This keeps enterprise business logic inside normal backend services.

---

## 2. Separate Live Data from Knowledge

```text
Live transactional information
            ↓
           MCP

Enterprise documentation
            ↓
           RAG
```

This allows the system to use the appropriate source for each type of information.

---

## 3. Ground AI Responses

The system is designed to avoid hallucinating enterprise information.

The assistant should:

1. Identify what information is required.
2. Select the appropriate tool.
3. Retrieve the information.
4. Use retrieved results as context.
5. Clearly state when available information is insufficient.

---

## 4. MCP as the AI Tool Boundary

MCP provides a standardized interface between AI applications and tools.

```text
AI Application
      │
      │ MCP
      ▼
Tool Provider
```

This allows the AI backend to interact with enterprise capabilities without embedding every enterprise integration directly into the AI application.

---

## 5. Agent Orchestration is Separate from MCP

MCP answers:

> "How can an AI application communicate with tools?"

The agent layer answers:

> "What should the AI do next?"

This distinction is fundamental to the architecture.

---

# What This Project Demonstrates

This project combines several concepts that are commonly treated independently:

* Traditional Spring Boot backend development
* Enterprise domain modeling
* REST APIs
* PostgreSQL
* Spring AI
* Local LLM inference with Ollama
* MCP server development
* MCP client integration
* AI tool calling
* RAG
* Vector search
* pgvector
* Prompt engineering
* Agentic orchestration
* Multi-step tool execution
* Grounded AI responses
* Separation of live data and enterprise knowledge

The project therefore evolves from:

```text
Traditional Backend
       ↓
Business Tools
       ↓
MCP
       ↓
RAG
       ↓
LLM Integration
       ↓
AI Backend
       ↓
Agentic Enterprise Assistant
```

---

# Running the Complete System

The complete system consists of multiple services.

A typical startup sequence is:

```text
1. PostgreSQL
      │
      ├── Enterprise database
      └── pgvector database
       
2. Phase 1 Order Service
       
3. Phase 2 MCP Server
       
4. Phase 3 RAG Service
       
5. Ollama / Local LLM
       
6. Enterprise AI Backend
```

The AI backend then connects to the MCP and RAG services and makes their capabilities available to the AI layer.

---

# Project Evolution

```text
Phase 1
Enterprise Order Backend
        │
        ▼
Phase 2
MCP Tool Layer
        │
        ▼
Phase 3
RAG Knowledge Layer
        │
        ▼
Phase 4
Spring AI + Ollama
        │
        ▼
Phase 5
Enterprise AI Backend
        │
        ▼
Phase 6
Agentic Multi-Step Orchestration
```

The final architecture demonstrates how a conventional enterprise backend can be incrementally extended into an AI-powered system without putting the LLM directly inside the core business/data layer.

---

# Learning Objectives

The project is designed as a practical exploration of:

### Backend Engineering

* Spring Boot
* REST APIs
* Service/repository architecture
* PostgreSQL
* Enterprise domain modeling

### AI Engineering

* LLM integration
* Ollama
* Spring AI
* Prompt design
* Tool calling
* Agent loops

### RAG

* Document ingestion
* Chunking
* Embeddings
* Vector search
* pgvector
* Retrieval

### MCP

* MCP server
* MCP client
* Tool discovery
* Tool schemas
* Tool invocation
* Stateless/streamable MCP communication

### System Design

* Service boundaries
* AI orchestration
* Tool abstraction
* Separation of concerns
* Live data vs knowledge retrieval
* Grounded AI architecture

---

# Final Architecture

```text
                         ┌─────────────────┐
                         │      User       │
                         └────────┬────────┘
                                  │
                                  ▼
                  ┌────────────────────────────┐
                  │    Enterprise AI Backend   │
                  │                            │
                  │      Spring AI             │
                  │      Agent Orchestration   │
                  └─────────────┬──────────────┘
                                │
                 ┌──────────────┼──────────────┐
                 │              │              │
                 ▼              ▼              ▼
             Ollama        MCP Client      RAG Client
                 │              │              │
                 │              ▼              ▼
                 │         MCP Server      RAG Service
                 │              │              │
                 │              ▼              ▼
                 │       Enterprise API    pgvector
                 │              │              │
                 │              ▼              │
                 │         PostgreSQL ◄────────┘
                 │
                 └──────────────┐
                                │
                                ▼
                           Final Answer
```

---

## Status

The project is developed incrementally from Phase 1 through Phase 6, with each phase building on the previous architectural layer.

The final goal is a practical **enterprise AI operations assistant** that combines traditional backend systems, MCP-based tools, RAG, local LLM inference, and agentic orchestration into a single cohesive architecture.
