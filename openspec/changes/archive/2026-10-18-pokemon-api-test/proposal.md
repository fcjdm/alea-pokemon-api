# Proposal

## Why

Alea's technical assessment requires a production-grade, maintainable REST API to expose statistical Pokémon rankings by consuming the public PokéAPI. This service enables clients to easily query top-ranking Pokémon across key physical and combat attributes without having to fetch and aggregate large volumes of raw Pokémon data themselves.

## What Changes

- Implement an API-first OpenAPI 3.0 contract (`src/main/resources/openapi/openapi.yml`) specifying endpoints for Pokémon rankings.
- Expose three REST endpoints under `/api/v1/pokemon`:
  - `GET /api/v1/pokemon/heaviest`: Top 5 heaviest Pokémon.
  - `GET /api/v1/pokemon/highest`: Top 5 highest Pokémon.
  - `GET /api/v1/pokemon/experience`: Top 5 Pokémon with highest base experience.
- Implement Clean Architecture layer separation across `domain`, `application`, `infrastructure`, and `presentation`.
- Implement Adapter Pattern for external PokéAPI integration using Spring `RestClient` with resilience via Spring Cache (`@Cacheable`).
- Implement Strategy Pattern for pluggable, extensible sorting and ranking criteria.
- Add RFC 7807 compliant centralized error handling using `@ControllerAdvice` and `ProblemDetail`.
- Enforce strict test-driven quality with JUnit 5, Mockito, and MockMvc, adhering to a minimum of 90% instruction coverage monitored via JaCoCo.
- Provide production containerization with a multi-stage `Dockerfile`.
- Provide comprehensive `README.md` documentation covering architectural decisions, setup/execution, and future improvements.

## Capabilities

### New Capabilities
- `pokemon-ranking`: Exposes statistical rankings (top 5 heaviest, highest, and highest base experience) fetched from PokéAPI with caching and standardized error responses.

### Modified Capabilities
None.

## Impact

- **New APIs**: New `/api/v1/pokemon/heaviest`, `/api/v1/pokemon/highest`, and `/api/v1/pokemon/experience` HTTP endpoints.
- **Dependencies**: Uses existing Spring Boot 3.3.4 dependencies (`spring-boot-starter-web`, `spring-boot-starter-cache`, `spring-boot-starter-validation`, `springdoc-openapi-starter-webmvc-ui`, `lombok`, `jacoco-maven-plugin`).
- **External Integration**: Outbound HTTPS calls to PokéAPI (`https://pokeapi.co/api/v2/`).
- **DevOps**: Multi-stage Docker container build.

