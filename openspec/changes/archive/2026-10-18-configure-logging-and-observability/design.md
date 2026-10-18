# Design

## Context

The service is a Spring Boot 3.3 REST API with Clean Architecture. While SLF4J `@Slf4j` is present in adapters and services, `PokemonController` currently logs at `DEBUG` while the default Spring Boot level is `INFO`. Furthermore, there is no HTTP request/response logging filter or correlation ID tracking across threads.

## Goals / Non-Goals

**Goals:**
- Provide clear runtime logging for incoming HTTP requests, processing times, and status codes.
- Provide request tracing via a correlation ID (`X-Correlation-Id`) added to SLF4J MDC and HTTP response headers.
- Configure clean console logging levels and pattern in `application.yaml`.
- Ensure 100% test pass rate and maintain JaCoCo instruction coverage >= 90%.

**Non-Goals:**
- Distributed OpenTelemetry / Zipkin tracing export (out of scope for this technical assessment).
- External log shipping agents (ELK / Logstash / Datadog).

## Decisions

### 1. `OncePerRequestFilter` for Request Logging and Correlation ID
- **Decision:** Implement `RequestLoggingFilter extends OncePerRequestFilter` in `presentation.filter`.
- **Rationale:** Ensures every HTTP request passes through exactly once. It captures an incoming `X-Correlation-Id` header or generates a `UUID`, stores it in `MDC.put("correlationId", ...)`, adds `response.setHeader("X-Correlation-Id", ...)`, measures elapsed time with `System.currentTimeMillis()`, and logs the request summary before cleaning up MDC in a `finally` block.
- **Alternatives considered:** Spring AOP around `@RestController` (misses filter errors and header manipulation) vs. CommonsRequestLoggingFilter (lacks correlation ID and latency logging).

### 2. Standardized Logging Levels & Pattern in `application.yaml`
- **Decision:** Configure `logging.level.com.alea.pokemon: INFO` (with debug capability) and `logging.pattern.console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{correlationId:-none}] - %msg%n"`.
- **Rationale:** Uses standard Spring Boot Logback integration without requiring XML files, making it simple, portable, and easily overridable via environment variables.

### 3. Controller Log Level Alignment
- **Decision:** Update `PokemonController` logging to `INFO` level so endpoint calls are visible in standard application output alongside the request filter.

## Risks / Trade-offs

- [Risk: MDC ThreadLocal leak in thread pools] → **Mitigation**: Always invoke `MDC.clear()` inside the filter's `finally` block.
- [Risk: Performance overhead of logging] → **Mitigation**: Log only high-level request metadata (method, URI, status, duration) rather than full payload bodies.

