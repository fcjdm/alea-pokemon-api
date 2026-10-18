# Proposal

## Why

The application currently lacks runtime logging visibility: `PokemonController` logs at `DEBUG` level while the default root level is `INFO`, no custom logging levels are defined in `application.yaml`, and incoming HTTP requests are not logged with operational metadata (HTTP method, path, status, latency, request ID). Adding structured logging and HTTP request/response tracking provides operators and developers with observability, debugging capabilities, and audit trails.

## What Changes

- Configure application logging in `application.yaml` with explicit log levels for `com.alea.pokemon` (`INFO` or `DEBUG` configurable) and formatted log pattern.
- Implement an HTTP request/response logging filter (`RequestLoggingFilter`) that generates/propagates a `X-Correlation-Id` header and sets it in SLF4J MDC (Mapped Diagnostic Context).
- Log incoming HTTP request method, URI, response status code, and processing duration in milliseconds.
- Add comprehensive unit and integration tests for logging and filter execution ensuring JaCoCo >= 90% coverage.

## Capabilities

### New Capabilities
- `observability-logging`: Centralized request/response logging, correlation ID injection via MDC, and configurable logging levels.

### Modified Capabilities
None.

## Impact

- Configuration: Updates `application.yaml` with `logging:` configuration.
- Presentation/Infrastructure: Adds `RequestLoggingFilter` registering HTTP access logs and `X-Correlation-Id`.
- API responses: Includes `X-Correlation-Id` header in all responses for traceability.
- Dependencies: Built-in Spring Boot, Servlet API, and SLF4J (no new external dependencies needed).

