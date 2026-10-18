# Design

## Context

The application is a Spring Boot 3.3.4 (Java 17) service designed to provide statistical rankings for Pokémon by fetching data from the public PokéAPI. Per `AGENT_RULES.md`, the implementation must adhere to Clean Architecture, SOLID principles, OpenAPI-first API contracts, Spring caching, strict JaCoCo test coverage ($\ge 90\%$), RFC 7807 error responses, and multi-stage Docker containerization.

## Goals / Non-Goals

**Goals:**
- Enforce strict Clean Architecture boundaries isolating business logic from external frameworks and transport layers.
- Provide contract-driven API development using an OpenAPI 3.0 specification (`src/main/resources/openapi/openapi.yml`).
- Use the Adapter Pattern to isolate outbound HTTP communication with PokéAPI and Strategy Pattern to modularize ranking algorithms.
- Protect external PokéAPI resources and optimize response latencies using Spring Cache (`@Cacheable`).
- Return RFC 7807 compliant error details through centralized exception handling (`@ControllerAdvice` with `ProblemDetail`).
- Achieve and guarantee $\ge 90\%$ JaCoCo instruction coverage with unit and WebMvc integration tests.
- Package the service into an optimized, lightweight multi-stage Docker container image.

**Non-Goals:**
- Implementing persistence/database storage (RDBMS/NoSQL) — Pokémon data is sourced dynamically from PokéAPI.
- Distributed caching (e.g., Redis cluster) in this phase — simple in-memory Spring Cache with `ConcurrentMapCacheManager` suffices for the assessment, with Redis documented as a future enhancement.
- Implementing authentication/authorization or public rate-limiting at this stage.

## Decisions

### 1. Clean Architecture Layering & Package Structure
The codebase will be organized into four isolated layers under `com.alea.pokemon`:
- `domain`:
  - Entities & Value Objects: `Pokemon` (fields: `id`, `name`, `weight`, `height`, `baseExperience`).
  - Ports: `PokemonPort` (interface for retrieving Pokémon data).
  - Exceptions: `PokemonNotFoundException`, `PokemonServiceUnavailableException`.
  - Zero external framework dependencies (pure Java).
- `application`:
  - Port / Use Case: `GetPokemonRankingsUseCase` or `PokemonRankingService`.
  - Strategy Pattern: `RankingStrategy` interface implemented by `HeaviestRankingStrategy`, `HighestRankingStrategy`, and `ExperienceRankingStrategy`.
  - DTOs: `PokemonResponseDto` (presentation mapping).
- `infrastructure`:
  - PokéAPI Client Adapter: `PokeApiClientAdapter` implementing `PokemonPort` using Spring 6's modern `RestClient`.
  - External DTOs: `PokeApiPokemonResponse`, `PokeApiNamedResourceListResponse`, etc.
  - Configuration: `CacheConfig` (`@EnableCaching`, cache name definitions), `PokeApiProperties`, and `RestClientConfig`.
- `presentation`:
  - Controllers: `PokemonController` handling `/api/v1/pokemon/*` routes.
  - Exception Handling: `GlobalExceptionHandler` with `@ExceptionHandler` mapping domain/infrastructure errors to standard Spring `ProblemDetail` (RFC 7807).

*Alternatives considered:*
- Traditional layered architecture (Controller-Service-Repository): Rejected because it couples business logic to Spring annotations and external REST entities.

### 2. API-First Specification (OpenAPI 3.0)
Define the API contract in `src/main/resources/openapi/openapi.yml` covering:
- `/api/v1/pokemon/heaviest`
- `/api/v1/pokemon/highest`
- `/api/v1/pokemon/experience`
Each with schemas for `PokemonResponse`, `ProblemDetail` responses, and HTTP response codes (200, 500, 502, 503). Springdoc OpenAPI Starter WebMvc UI exposes the interactive Swagger UI at `/swagger-ui.html`.

*Alternatives considered:*
- Code-first annotation-based generation: Rejected to ensure strict adherence to Spec-Driven Development and the API-first mandate in `AGENT_RULES.md`.

### 3. Outbound Client: Spring `RestClient` with Adapter Pattern
Use Spring Boot 3's synchronous `RestClient` configured with timeout constraints and base URL (`https://pokeapi.co/api/v2/`). The adapter encapsulates mapping from external PokéAPI payloads to internal `domain.Pokemon` entities.

*Alternatives considered:*
- `RestTemplate`: In maintenance mode; `RestClient` is the modern fluent client in Spring Boot 3.x.
- `WebClient`: Adds unnecessary reactive overhead and Netty runtime complexity for a standard synchronous REST service.

### 4. Ranking Logic with Strategy Pattern
Create an interface:
```java
public interface RankingStrategy {
    RankingCriteria getCriteria();
    List<Pokemon> rank(List<Pokemon> pokemons, int limit);
}
```
Implement separate strategies for `HEAVIEST`, `HIGHEST`, and `EXPERIENCE`. The application service resolves strategies via a strategy registry or Spring dependency injection map.

*Alternatives considered:*
- Ad-hoc sorting methods inside a single service class: Rejected because the Strategy Pattern provides Open-Closed Principle compliance, making criteria easily extensible and individually unit-testable.

### 5. Caching Strategy
Annotate client fetching methods or ranking use cases with `@Cacheable(value = "pokemonRankings", key = "#criteria")` or `@Cacheable("pokemons")`. On application startup or first query, data fetched from PokéAPI is cached in-memory.

*Alternatives considered:*
- External Redis: Overkill for local execution without Docker Compose dependencies in standard build tests; documented as a future enhancement in `README.md`.

### 6. Error Handling Strategy (RFC 7807)
Implement `GlobalExceptionHandler` extending `ResponseEntityExceptionHandler` or using `@RestControllerAdvice`:
- `PokemonNotFoundException` $\rightarrow$ 404 ProblemDetail
- `PokemonServiceUnavailableException` $\rightarrow$ 502/503 ProblemDetail
- `Exception` (fallback) $\rightarrow$ 500 ProblemDetail

### 7. Multi-stage Docker Build
Create a root `Dockerfile` using:
- **Build stage:** `maven:3.9-eclipse-temurin-17-alpine` or lightweight eclipse-temurin to build the jar with Maven wrapper/offline dependencies.
- **Runtime stage:** `eclipse-temurin:17-jre-alpine` (or distroless), running as a non-root user, exposing port 8080.

## Risks / Trade-offs

- [Risk] External PokéAPI rate limiting or slow pagination when fetching list of Pokémon.
  $\rightarrow$ *Mitigation:* Implement caching with `@Cacheable` and fetch bounded sets or batch requests with timeouts configured on `RestClient`.
- [Risk] Code coverage drop below 90% threshold failing `./mvnw clean verify`.
  $\rightarrow$ *Mitigation:* Exclude pure boilerplate configurations/DTOs from JaCoCo if necessary, and write comprehensive unit tests for all strategies, mappers, client adapter, and controllers.
- [Risk] PokéAPI downtime during automated test execution.
  $\rightarrow$ *Mitigation:* Mock the external PokéAPI endpoints completely in tests using Mockito and MockRestServiceServer / MockMvc.

