# Tasks

## 1. OpenAPI 3.0 Contract Definition

- [x] 1.1 Create `src/main/resources/openapi/openapi.yml` defining paths for `/api/v1/pokemon/heaviest`, `/api/v1/pokemon/highest`, and `/api/v1/pokemon/experience`, schemas for `PokemonResponse` and RFC 7807 `ProblemDetail`, and verify OpenAPI spec validity
- [x] 1.2 Configure springdoc-openapi in `application.yaml` to serve Swagger UI and the OpenAPI spec, and verify Swagger UI endpoint `/swagger-ui.html` loads correctly

## 2. Domain Layer Implementation

- [x] 2.1 Implement core domain entity `Pokemon` with attributes `id`, `name`, `weight`, `height`, and `baseExperience`, and verify with unit tests
- [x] 2.2 Define domain port interface `PokemonPort` for querying external Pokémon information, and define domain exceptions `PokemonNotFoundException` and `PokemonServiceUnavailableException` with unit tests

## 3. Infrastructure Layer (PokéAPI Client Adapter & Caching)

- [x] 3.1 Create external PokéAPI response DTOs (`PokeApiPokemonResponse`, `PokeApiNamedResourceListResponse`, etc.) and verify JSON serialization/deserialization with unit tests
- [x] 3.2 Implement `PokeApiClientAdapter` implementing `PokemonPort` using Spring `RestClient`, with configurable timeout and base URL properties, and verify using `MockRestServiceServer` tests
- [x] 3.3 Configure Spring Cache (`@EnableCaching`, cache name `pokemonRankings` or `pokemons`), annotate client methods with `@Cacheable`, and verify caching behavior with unit/integration tests

## 4. Application Layer & Ranking Strategies

- [x] 4.1 Define `RankingStrategy` interface and implement concrete strategies: `HeaviestRankingStrategy`, `HighestRankingStrategy`, and `ExperienceRankingStrategy`, verifying each with unit tests for sorting and limit bounds
- [x] 4.2 Implement `PokemonRankingService` (or use cases) that orchestrates `PokemonPort` and the appropriate `RankingStrategy`, and maps entities to `PokemonResponseDto`, verifying logic with Mockito unit tests

## 5. Presentation Layer & RFC 7807 Centralized Error Handling

- [x] 5.1 Implement `PokemonController` mapping `/api/v1/pokemon/heaviest`, `/api/v1/pokemon/highest`, and `/api/v1/pokemon/experience`, delegating to application services, and verify endpoints using MockMvc tests
- [x] 5.2 Implement `GlobalExceptionHandler` using `@RestControllerAdvice` returning RFC 7807 `ProblemDetail` responses for domain exceptions and unexpected errors, and verify with MockMvc error test cases

## 6. Quality Assurance & JaCoCo Coverage Enforcement

- [x] 6.1 Implement comprehensive unit and WebMvc integration test suite covering edge cases (empty lists, upstream failures, cache hits/misses, parameter validations)
- [x] 6.2 Execute `./mvnw clean verify` (or `./mvnw.cmd clean verify`) and verify JaCoCo instruction coverage meets or exceeds 90% across the codebase

## 7. Containerization & Documentation

- [x] 7.1 Create a multi-stage production-ready `Dockerfile` in the project root with builder and runtime stages (Eclipse Temurin 17 JRE / non-root user), and verify Docker build succeeds where Docker is available in the runtime environment
- [x] 7.2 Create comprehensive `README.md` covering Architecture & Design Decisions (Clean Architecture, SDD/OpenSpec, OpenAPI-first, Spring Caching, error handling), Setup & Execution (`mvnw`, tests, Docker), and Future Improvements (Redis, Resilience4j, Security, Observability, K8s)
- [x] 7.3 Execute final verification build (`.\mvnw.cmd clean verify`) to ensure all tests, coverage checks, and packaging pass cleanly

