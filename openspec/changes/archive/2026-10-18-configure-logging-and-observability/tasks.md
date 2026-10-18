# Tasks

## 1. Logging Configuration

- [x] 1.1 Configure `logging:` in `application.yaml` with explicit package level for `com.alea.pokemon` and console pattern including correlation ID, and promote `PokemonController` logging to INFO
- [x] 1.2 Verify configuration properties and logging format with unit/integration tests

## 2. Request Logging & Correlation ID Filter

- [x] 2.1 Implement `RequestLoggingFilter` extending `OncePerRequestFilter` to manage `X-Correlation-Id` in MDC, set response header, and log method, path, status, and duration
- [x] 2.2 Create unit tests for `RequestLoggingFilter` verifying UUID generation, existing correlation ID preservation, MDC cleanup, and error status handling

## 3. Integration & Coverage Verification

- [x] 3.1 Update `PokemonControllerTest` or add filter integration tests to verify the `X-Correlation-Id` header is returned on API responses
- [x] 3.2 Execute `./mvnw clean verify` to ensure all tests pass and JaCoCo coverage meets or exceeds 90%

