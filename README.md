# Alea Pokemon API 🎮⚡

> A production-ready Spring Boot 3 REST API consuming [PokéAPI](https://pokeapi.co/api/v2/) to rank the top 5 Pokémon by weight, height, and base experience.

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Coverage ≥ 90%](https://img.shields.io/badge/JaCoCo_Coverage->90%25-brightgreen)](https://www.jacoco.org/jacoco/)
[![OpenAPI 3.0](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=openapiinitiative&logoColor=white)](https://swagger.io/specification/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

---

🌐 **Language / Idioma**: [English](#-english) | [Español](#-español)

---

# 🇬🇧 English

## 1. Challenge Compliance

Developed to address every scenario and technical requirement from the **Alea Knowledge Test**:

| Requirement | Implementation | Status |
|---|---|:---:|
| **Top 5 Heaviest** | `GET /api/v1/pokemon/heaviest` | ✅ |
| **Top 5 Highest** | `GET /api/v1/pokemon/highest` | ✅ |
| **Top 5 Base Experience** | `GET /api/v1/pokemon/experience` | ✅ |
| **Data Source** | PokéAPI (`https://pokeapi.co/api/v2/`) via `RestClient` | ✅ |
| **Must have: Java / Spring Boot** | Java 17 + Spring Boot 3.3.4 | ✅ |
| **Must have: Coverage ≥ 90%** | JaCoCo Quality Gate enforced on `mvn verify` (> 90% instruction coverage) | ✅ |
| **Nice to have: Integration Tests** | `MockMvc` web layer integration tests, context loading, and config tests | ✅ |
| **Nice to have: Production Ready** | Multi-stage Docker, RFC 7807 error handling, request logging filter, caching, timeouts | ✅ |

---

## 2. Architecture & Design Decisions (The "Why" Behind the Code)

When designing this service, my goal wasn't just to make it pass the test scenarios, but to build it with the care, clarity, and maintainability expected in an enterprise backend. Here is how I approached the design:

### SOLID Principles in Action
- **Single Responsibility (SRP):** Each layer and class does exactly one job. Domain models manage business state, strategies handle sorting, controllers route HTTP requests, and adapters handle external communication.
- **Open/Closed (OCP):** The ranking logic uses the **Strategy Pattern** (`RankingStrategy`). If tomorrow product requirements ask for the "Top 5 fastest Pokémon" or "Top 5 with highest defense", we only need to add a new strategy class—the core service and existing strategies remain untouched.
- **Liskov Substitution (LSP):** All ranking strategies (`Heaviest`, `Highest`, `Experience`) implement the same contract and can be swapped seamlessly by the ranking service.
- **Interface Segregation (ISP):** Instead of bloated interfaces, we use targeted abstractions such as `PokemonPort` and dedicated generated API contracts.
- **Dependency Inversion (DIP):** The high-level application service (`PokemonRankingService`) depends on the domain abstraction `PokemonPort`, never directly on the concrete infrastructure client (`PokeApiClientAdapter`).

### Clean / Hexagonal Architecture
I decoupled the core business domain from infrastructure and frameworks:
- **`domain`**: Pure Java business entity (`Pokemon`), ports (`PokemonPort`), and domain exceptions. Zero dependencies on Spring or HTTP libraries.
- **`application`**: Orchestration service (`PokemonRankingService`), DTOs, and the ranking strategies.
- **`infrastructure`**: PokéAPI integration via Spring 6 `RestClient`, timeout configs, and Spring Cache.
- **`presentation`**: REST controller implementing the generated API contract, request logging filter, and exception translators.

### AI-Assisted Development (Antigravity & OpenSpec)
This service was built using **Google Antigravity** as an AI pair-programming assistant under a structured spec-driven workflow:
- **`AGENT_RULES.md`:** Establishes non-negotiable architectural rules (Clean Architecture, API-First, TDD, and JaCoCo coverage > 90%).
- **OpenSpec:** Deconstructs requirements into formal specifications and iterative tasks, ensuring full requirement traceability.

### API-First Design (OpenAPI 3.0)
Before writing any controller logic, I designed the contract in `src/main/resources/openapi/openapi.yml`. 
Using `openapi-generator-maven-plugin`, the interfaces (`PokemonApi`) and DTOs (`PokemonResponse`) are automatically generated at compile time. This ensures 100% contract compliance, eliminates synchronization drift between frontend/backend, and instantly exposes live documentation via **Swagger UI**.

### Test-Driven Development (TDD) & Quality Gate (> 90% Coverage)
Testing was an integral part of the development cycle, following a TDD mindset:
1. **Unit Tests:** Validating domain rules, ranking strategies, and DTO mappings in complete isolation using JUnit 5 and AssertJ.
2. **Integration Tests (Mocked External Boundary):** Leveraging `MockMvc` to test controller routing, JSON serialization, HTTP status codes, and global error handling. 
   - *Why mock external PokéAPI calls?* Real network calls in automated tests make test suites fragile, slow, and reliant on an external service that may experience outages, rate-limiting, or connectivity issues. By mocking the external boundary, development, local testing, and CI/CD pipelines can proceed reliably 100% of the time, even if PokéAPI is temporarily down or offline.
3. **JaCoCo Quality Gate:** Configured to fail the Maven build (`mvn verify`) if instruction coverage falls below 90% (excluding generated boilerplate).

### Performance, Caching & Upstream Resilience
PokéAPI is an external public API. Hitting it on every incoming request would introduce high latency and risk rate-limiting (`429 Too Many Requests`).
- **In-Memory Caching (`@Cacheable`):** The adapter caches Pokémon resources (`pokemon-list` and `pokemon-detail`), slashing subsequent query times from seconds to sub-milliseconds.
- **Resilient Timeouts:** Configured explicit connect (5s) and read (10s) timeouts via `PokeApiProperties` to ensure downstream issues don't hang server threads.
- **RFC 7807 Standardized Errors:** A `@RestControllerAdvice` translates domain exceptions into standard `ProblemDetail` payloads without leaking internal stack traces.
- **Request Logging Filter:** Measures and logs method, URI, HTTP status, and duration in ms for every incoming request.

---

## 3. Future Improvements (What I'd Love to Add With More Time)

If this project were heading to a high-traffic production environment tomorrow, these are the improvements I would prioritize:

1. **Distributed Caching with Redis**: 
   Right now, the cache is held in the JVM memory of each instance. In a horizontally scaled Kubernetes cluster with multiple pods, migrating to Redis (`spring-boot-starter-data-redis`) with configurable TTLs would keep cache hits consistent across all pods and survive container restarts.
2. **Circuit Breakers & Rate Limiting with Resilience4j**:
   Adding a Circuit Breaker around the PokéAPI client would allow us to fail fast and serve cached stale data or a graceful fallback if PokéAPI suffers an outage, preventing thread exhaustion.
3. **Parallel Fetching (Virtual Threads) & Cache Warming**:
   The initial cold request has to fetch details for multiple Pokémon sequentially. By taking advantage of **Java 21 Virtual Threads** or reactive streams, we could fetch details concurrently. Furthermore, a background startup runner could "pre-warm" the cache so no real user ever suffers cold-start latency.
4. **Full Observability (Actuator, Prometheus & Grafana)**:
   Exposing Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/metrics`) and streaming metrics to Prometheus would allow monitoring cache hit rates, external API latencies, and traffic spikes in real-time dashboards.
5. **Security & API Rate Limiting**:
   Securing the endpoints with Spring Security (OAuth2/JWT) and adding client-side rate limiting (e.g. Bucket4j) per IP or API key to prevent abusive usage.
6. **Local Database Synchronization**:
   Periodically synchronizing Pokémon data into a local database would make our API 100% resilient and independent of PokéAPI availability during live queries.

---

## 4. Quickstart

### Run Locally
```bash
./mvnw clean spring-boot:run        # Linux / macOS
.\mvnw.cmd clean spring-boot:run    # Windows
```

### Run Tests & Verify JaCoCo Coverage (> 90%)
```bash
./mvnw clean verify                 # Linux / macOS
.\mvnw.cmd clean verify             # Windows
```
*HTML report generated at `target/site/jacoco/index.html`.*

### Docker & Swagger UI
```bash
docker build -t alea-pokemon-api .
docker run --rm -p 8080:8080 alea-pokemon-api
```
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Example Requests
```bash
curl "http://localhost:8080/api/v1/pokemon/heaviest"
curl "http://localhost:8080/api/v1/pokemon/highest"
curl "http://localhost:8080/api/v1/pokemon/experience"
```

---
---

# 🇪🇸 Español

## 1. Cumplimiento del Desafío

Desarrollado para cubrir todos los escenarios y requisitos técnicos planteados en el **Alea Knowledge Test**:

| Requisito | Implementación | Estado |
|---|---|:---:|
| **Top 5 más pesados** | `GET /api/v1/pokemon/heaviest` | ✅ |
| **Top 5 más altos** | `GET /api/v1/pokemon/highest` | ✅ |
| **Top 5 más experiencia base** | `GET /api/v1/pokemon/experience` | ✅ |
| **Fuente de datos** | PokéAPI (`https://pokeapi.co/api/v2/`) mediante `RestClient` | ✅ |
| **Must have: Java / Spring Boot** | Java 17 + Spring Boot 3.3.4 | ✅ |
| **Must have: Cobertura ≥ 90%** | Quality Gate de JaCoCo en `mvn verify` (> 90% en cobertura de instrucciones) | ✅ |
| **Nice to have: Tests de integración** | Tests de capa web con `MockMvc`, carga de contexto y configuración | ✅ |
| **Nice to have: Production Ready** | Docker multi-etapa, errores RFC 7807, filtro de logging, caché y timeouts | ✅ |

---

## 2. Arquitectura y Decisiones de Diseño (El "Por qué" del Código)

Al plantear la solución, mi objetivo no era únicamente hacer que pasasen los escenarios de la prueba, sino construir el servicio con el nivel de robustez, claridad y mantenibilidad que requiere un entorno de back real. Estas fueron las decisiones clave:

### Principios SOLID en la Práctica
- **Single Responsibility (SRP):** Cada capa y clase tiene una única responsabilidad. Los modelos de dominio encapsulan las reglas del negocio, las estrategias gestionan el ordenamiento, los controladores enrutan HTTP y los adaptadores gestionan la comunicación con PokéAPI.
- **Open/Closed (OCP):** El motor de rankings se basa en el **Patrón Strategy** (`RankingStrategy`). Si el día de mañana se nos pide ordenar por "Top 5 más veloces" o "Top 5 con mayor defensa", únicamente creamos una nueva clase de estrategia; el servicio de rankings y las estrategias existentes no necesitan ninguna modificación.
- **Liskov Substitution (LSP):** Todas las estrategias (`Heaviest`, `Highest`, `Experience`) implementan el mismo contrato y pueden sustituirse entre sí limpiamente.
- **Interface Segregation (ISP):** Interfaces pequeñas y específicas (`PokemonPort`, contratos OpenAPI) en lugar de interfaces genéricas sobrecargadas.
- **Dependency Inversion (DIP):** El servicio de aplicación (`PokemonRankingService`) depende de la abstracción del dominio (`PokemonPort`), y nunca directamente del cliente de infraestructura (`PokeApiClientAdapter`).

### Arquitectura Limpia / Hexagonal
Aislar el dominio de negocio de los detalles de infraestructura y frameworks:
- **`domain`**: Entidad pura de negocio (`Pokemon`), puertos (`PokemonPort`) y excepciones de dominio. Cero dependencias de librerías web o Spring.
- **`application`**: Servicio orquestador (`PokemonRankingService`), DTOs y estrategias de ordenamiento.
- **`infrastructure`**: Conexión a PokéAPI mediante `RestClient` de Spring 6, configuración de timeouts y caché.
- **`presentation`**: Controlador REST que implementa el contrato generado, filtro de logging de tiempos y manejador global de excepciones.

### Desarrollo Asistido por IA (Antigravity y OpenSpec)
El servicio se construyó utilizando **Google Antigravity** como asistente de pair-programming mediante un flujo guiado por especificaciones:
- **`AGENT_RULES.md`:** Define las restricciones arquitectónicas esenciales (Arquitectura Limpia, API-First, TDD y cobertura JaCoCo > 90%).
- **OpenSpec:** Desglosa los requisitos en especificaciones formales y tareas incrementales, asegurando la trazabilidad de la solución.

### Enfoque API-First (OpenAPI 3.0)
Antes de escribir una sola línea en el controlador, diseñé el contrato en `src/main/resources/openapi/openapi.yml`.
Con el plugin `openapi-generator-maven-plugin`, las interfaces (`PokemonApi`) y los DTOs (`PokemonResponse`) se autogeneran en tiempo de compilación. Esto asegura fidelidad total al contrato, evita desajustes entre frontend y backend, y proporciona documentación interactiva inmediata con **Swagger UI**.

### Test-Driven Development (TDD) y Quality Gate (> 90% Cobertura)
Las pruebas fueron el núcleo del desarrollo, siguiendo una filosofía TDD:
1. **Tests Unitarios:** Verificación exhaustiva de entidades, estrategias y DTOs de forma aislada con JUnit 5 y AssertJ.
2. **Tests de Integración (Frontera Externa Mockeada):** Uso de `MockMvc` para validar el enrutamiento HTTP, la serialización JSON, los códigos de respuesta y la gestión de excepciones.
   - *¿Por qué mockear las llamadas externas a PokéAPI?* Depender de peticiones reales en los tests hace que la suite de pruebas sea lenta, frágil e inestable ante caídas o límites de cuota de un tercero. Al aislar y mockear la interacción externa, garantizamos que el equipo pueda seguir desarrollando, compilando y ejecutando la suite de CI/CD de manera rápida y determinista al 100%, incluso si la PokéAPI experimenta caídas, latencia o si estamos sin conexión.
3. **Quality Gate con JaCoCo:** La fase `mvn verify` valida que la cobertura de instrucciones supere estrictamente el 90% (excluyendo código autogenerado), haciendo fallar el build si no se cumple.

### Rendimiento, Caché y Protección de PokéAPI
PokéAPI es un servicio externo público con latencias y límites de tráfico. Llamarlo en cada petición penalizaría el tiempo de respuesta y arriesgaría errores de saturación (`429 Too Many Requests`).
- **Caché en Memoria (`@Cacheable`):** El adaptador cachea tanto la lista como los detalles individuales, reduciendo los tiempos en peticiones recurrentes de varios segundos a menos de un milisegundo.
- **Timeouts Resilientes:** Timeouts configurados explícitamente (5s de conexión, 10s de lectura) para evitar que hilos del servidor queden bloqueados ante problemas del upstream.
- **Errores Estándar RFC 7807:** Un `@RestControllerAdvice` traduce los fallos de negocio e infraestructura a payloads limpios `ProblemDetail` sin exponer trazas internas.
- **Filtro de Registro de Peticiones:** Mide y registra método, URI, código HTTP y tiempo de respuesta en milisegundos para cada petición entrante.

---

## 3. Mejoras Futuras (Lo que me habría gustado añadir con más tiempo)

Si tuviese que llevar este proyecto a producción con alto volumen de tráfico, estas serían las mejoras que priorizaría:

1. **Caché Distribuida con Redis**:
   Actualmente la caché reside en la memoria de la instancia. En un cluster de Kubernetes con múltiples pods escalados horizontalmente, migrar a Redis (`spring-boot-starter-data-redis`) con políticas de TTL permitiría compartir la caché entre todos los pods y sobrevivir a reinicios de contenedores.
2. **Circuit Breakers y Limitación con Resilience4j**:
   Implementar un Circuit Breaker sobre el cliente de PokéAPI para abrir el circuito de forma inmediata si la API externa cae o se degrada, devolviendo datos en caché o una respuesta controlada y protegiendo los recursos del sistema.
3. **Descarga en Paralelo (Virtual Threads) y Precalentamiento de Caché**:
   La primera petición en frío descarga los detalles de los Pokémon secuencialmente. Utilizando los **Virtual Threads de Java 21** o WebClient reactivo, podríamos obtener los detalles de forma concurrente. Además, un proceso de arranque (`ApplicationRunner`) podría precargar la caché para que ningún usuario experimente latencia en frío.
4. **Observabilidad Completa (Actuator, Prometheus y Grafana)**:
   Exponer métricas con Spring Boot Actuator y Prometheus (`/actuator/metrics`) para monitorizar en tiempo real el ratio de acierto de caché, los tiempos de llamada a PokéAPI y el tráfico entrante.
5. **Seguridad y Control de Peticiones (Rate Limiting)**:
   Añadir autenticación con Spring Security (OAuth2/JWT) y control de tasa de peticiones con Bucket4j por IP o cliente para evitar ataques o abusos.
6. **Sincronización en Base de Datos Local**:
   Sincronizar periódicamente los datos en una base de datos local para que las consultas de rankings se resuelvan de forma 100% autónoma y offline, sin depender en tiempo real de la disponibilidad de PokéAPI.

---

## 4. Guía Rápida

### Ejecución Local
```bash
./mvnw clean spring-boot:run        # Linux / macOS
.\mvnw.cmd clean spring-boot:run    # Windows
```

### Ejecutar Tests y Cobertura JaCoCo (> 90%)
```bash
./mvnw clean verify                 # Linux / macOS
.\mvnw.cmd clean verify             # Windows
```
*Reporte disponible en `target/site/jacoco/index.html`.*

### Docker y Swagger UI
```bash
docker build -t alea-pokemon-api .
docker run --rm -p 8080:8080 alea-pokemon-api
```
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Ejemplos de Peticiones
```bash
curl "http://localhost:8080/api/v1/pokemon/heaviest"
curl "http://localhost:8080/api/v1/pokemon/highest"
curl "http://localhost:8080/api/v1/pokemon/experience"
```
