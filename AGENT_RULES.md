# Agent Execution Rules & Architectural Constraints

## 1. Project Goal
Build a production-ready, highly maintainable Spring Boot 3.3 REST API (Java 17) for the "Alea" technical assessment. The application consumes the external PokéAPI (https://pokeapi.co/api/v2/) to retrieve statistical rankings while adhering to Clean Architecture, SOLID principles, Spec-Driven Development (OpenSpec), and strict test coverage.

## 2. Core Architectural & Technical Requirements

### Architecture & Design
- **Clean Architecture Principles:** Enforce layer separation to isolate business logic:
  - `domain`: Core entities, value objects, domain exceptions, and repository/client interfaces.
  - `application`: Use cases, services, strategy patterns, and DTOs.
  - `infrastructure`: External client implementations (PokéAPI adapter), Spring Cache configurations, and framework-specific adapters.
  - `presentation`: REST Controllers and HTTP exception handlers mapped from the OpenAPI spec.
- **API-First Approach:** Define the OpenAPI 3.0 contract (`src/main/resources/openapi/openapi.yml`) prior to controller implementation.
- **Design Patterns:** Use the Adapter Pattern for external REST consumption and Strategy Pattern for Pokémon sorting/ranking criteria.

### Engineering & Quality Standards
- **Strict Code Coverage:** JaCoCo instruction coverage MUST be over 90%. Verify using `./mvnw clean verify` (or `.\mvnw.cmd clean verify`).
- **Test-Driven Standards:** Comprehensive unit testing with JUnit 5 & Mockito, plus WebLayer integration tests using MockMvc ("Nice to have").
- **Resilience & Performance:** Implement Spring Caching (`@Cacheable`) on external API calls to avoid unnecessary rate-limiting or network latency ("Production ready").
- **Centralized Error Handling:** Global `@ControllerAdvice` returning structured, standard RFC 7807 problem details for exceptions.
- **Containerization (DevOps):** Include a multi-stage `Dockerfile` in the root directory for optimized production builds ("Production ready").
- **Language & Documentation:** All code comments, Javadoc, logs, and documentation MUST be written in **English**.

## 3. Required Endpoints
- `GET /api/v1/pokemon/heaviest`: Top 5 heaviest Pokémons.
- `GET /api/v1/pokemon/highest`: Top 5 highest Pokémons.
- `GET /api/v1/pokemon/experience`: Top 5 Pokémons with highest base experience.

## 4. Documentation Requirements (README.md)
The `README.md` file MUST include:
1. **Architecture & Design Decisions:** Explain Clean Architecture, Spec-Driven Development (OpenSpec), OpenAPI-first design, Spring Caching strategy, and error handling.
2. **Setup & Execution:** Clear instructions on how to run the app using `./mvnw clean spring-boot:run` (or `.\mvnw.cmd clean spring-boot:run`), execute tests (`clean verify`), and run with Docker.
3. **Future Improvements:** Document enhancements to implement given more time (e.g., Redis distributed caching, Rate limiting / Resilience4j circuit breakers, Spring Security / OAuth2, Micrometer/Grafana observability, Kubernetes manifests).

## 5. Execution Workflow Checklist
1. Review OpenSpec proposal and tasks in `openspec/changes/pokemon-api-test/`.
2. Create `src/main/resources/openapi/openapi.yml`.
3. Implement domain models and port interfaces.
4. Implement infrastructure PokéAPI client with Spring `RestClient` / `WebClient` and Spring Cache.
5. Implement application services and presentation REST controllers.
6. Write unit and integration tests achieving $\ge 90\%$ JaCoCo coverage.
7. Create multi-stage `Dockerfile` in the root folder.
8. Generate the comprehensive `README.md` file.
9. Execute `./mvnw clean verify` (or `.\mvnw.cmd clean verify`) to ensure all rules pass.