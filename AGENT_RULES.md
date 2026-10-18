# Agent Execution Rules & Architectural Constraints

## 1. Project Goal
Build and maintain a production-ready, highly maintainable Spring Boot 3.3 REST API (Java 17) for the "Alea" technical assessment. The application consumes the external PokéAPI (https://pokeapi.co/api/v2/) to retrieve statistical rankings while adhering to Clean Architecture, SOLID principles, Spec-Driven Development (OpenSpec), and strict test coverage.

## 2. Core Architectural & Technical Requirements

### Architecture & Design
- **Clean Architecture Principles:** Enforce layer separation to isolate business logic:
  - `domain`: Core entities, value objects, domain exceptions, and repository/client interfaces. Pure Java, zero framework/HTTP dependencies.
  - `application`: Use cases, services, strategy patterns, and DTOs.
  - `infrastructure`: External client implementations (PokéAPI adapter), Spring Cache configurations, and framework-specific adapters.
  - `presentation`: REST Controllers, HTTP filters, and exception handlers mapped from the OpenAPI spec.
- **API-First Approach & Code Generation:** Define the OpenAPI 3.0 contract (`src/main/resources/openapi/openapi.yml`) prior to any controller implementation. Use `openapi-generator-maven-plugin` during the Maven build to automatically generate the API interfaces (`PokemonApi`) and DTOs (`PokemonResponse`). The contract is the strict single source of truth; controllers must implement the generated interfaces rather than defining manual route signatures.
- **Design Patterns (SOLID):** 
  - **Strategy Pattern:** All Pokémon sorting/ranking criteria must implement `RankingStrategy` as standalone Spring beans. Adding a new ranking criterion must never alter `PokemonRankingService` (Open/Closed Principle).
  - **Adapter Pattern:** External REST consumption must be isolated behind domain ports (`PokemonPort`).

### Engineering, Observability & Quality Standards
- **Structured Logging & Observability:**
  - Use SLF4J (`@Slf4j`) across all layers with appropriate log levels (`INFO` for request lifecycle/milestones, `DEBUG` for internal details, `WARN`/`ERROR` for failures).
  - Incoming HTTP requests must be intercepted via `RequestLoggingFilter` to capture HTTP method, URI, response status, and execution duration in milliseconds (`durationMs`).
  - Never leak internal stack traces or sensitive payloads in production logs or API responses.
- **External Configuration & Timeouts:**
  - Never hardcode URLs, timeouts, or batch sizes. All external settings must be encapsulated in validated `@ConfigurationProperties` (e.g., `PokeApiProperties`) and declared in `application.yaml`.
  - External HTTP clients (`RestClient`) must enforce explicit connect (e.g., 5s) and read (e.g., 10s) timeouts to prevent thread hanging.
- **Resilience & Performance (Caching):**
  - Implement Spring Caching (`@Cacheable`) on external API calls (`pokemon-list` and `pokemon-detail`) to avoid unnecessary upstream rate-limiting or network latency.
- **Centralized Error Handling (RFC 7807):**
  - Global `@RestControllerAdvice` (`GlobalExceptionHandler`) translating domain and infrastructure exceptions into structured RFC 7807 `ProblemDetail` responses.
- **Strict Code Coverage (Quality Gate):**
  - JaCoCo instruction coverage MUST stay over 90% across the project bundle. Verify using `./mvnw clean verify` (or `.\mvnw.cmd clean verify`).
  - Only generated OpenAPI classes, Lombok boilerplate, and pure configuration properties may be excluded from JaCoCo checks.
- **Testing Standards:**
  - **Unit Tests:** Pure JUnit 5 + Mockito for domain rules, strategies, and DTO mappings.
  - **Integration Tests:** Use `MockMvc` for web layer verification, JSON validation, and HTTP status codes.
  - **Test Isolation (No Live Network Calls):** Tests must never call the real PokéAPI. External boundaries must be mocked to ensure 100% reliable, fast, and offline test execution.
- **DevOps & Containerization:**
  - Maintain an optimized, secure multi-stage `Dockerfile` in the root directory.
- **Language & Documentation:**
  - All source code, comments, Javadoc, logs, and commit messages MUST be written in **English**.
  - `README.md` must be kept up-to-date and maintain both English and Spanish documentation.

## 3. OpenSpec Change Lifecycle
For any new feature, architectural modification, or refactor:
1. **Spec-First:** Create or update an OpenSpec change under `openspec/changes/<change-name>/` containing `proposal.md`, spec deltas under `specs/`, and an actionable `tasks.md`.
2. **Validate Before Coding:** Align on design and task breakdown before writing production code.
3. **Incremental Execution:** Execute tasks step-by-step, running tests at each phase.
4. **Archive & Sync:** Once all tasks and tests pass verify, archive or sync the change using OpenSpec tooling to maintain an audit trail.

## 4. Required Endpoint
- `GET /api/v1/pokemon?sortBy={weight|height|experience}`: Top 5 Pokémon sorted by the selected attribute; unsupported values return HTTP 400 with RFC 7807 Problem Details.

## 5. Documentation Requirements (README.md)
The `README.md` file MUST maintain:
1. **Challenge Compliance Matrix:** Explicit mapping of requirements against delivered code.
2. **Architecture & Design Decisions:** Narrative explaining SOLID, Clean Architecture, AI-assisted development (Antigravity & OpenSpec), OpenAPI-first, caching, and error handling.
3. **Future Improvements:** Document enhancements for enterprise scalability (e.g., Redis distributed caching, Resilience4j circuit breakers, Spring Security / OAuth2, Prometheus/Grafana observability, Kubernetes manifests).
4. **Execution Guide:** Instructions for running locally (`./mvnw clean spring-boot:run`), testing (`clean verify`), Docker, and Swagger UI.

## 6. Execution Workflow Checklist
1. Create or review the OpenSpec change in `openspec/changes/`.
2. Update the OpenAPI 3.0 contract in `src/main/resources/openapi/openapi.yml` if endpoints or DTOs change.
3. Compile and generate interfaces/DTOs with `openapi-generator-maven-plugin`.
4. Implement/update domain models and port interfaces.
5. Implement infrastructure adapters (PokéAPI, cache, properties, logging).
6. Implement application services and REST controllers implementing generated interfaces.
7. Write unit and integration tests (isolated, no real network calls).
8. Verify that instruction coverage is $\ge 90\%$ via `./mvnw clean verify`.
9. Update `README.md` (English and Spanish) and sync OpenSpec tasks.
10. Execute `./mvnw clean verify` (or `.\mvnw.cmd clean verify`) to ensure all rules pass.