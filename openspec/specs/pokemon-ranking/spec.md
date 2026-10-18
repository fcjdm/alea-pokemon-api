# pokemon-ranking Specification

## Purpose

Provides statistical ranking queries for Pokémon attributes such as weight, height, and base experience by consuming external Pokémon data, optimizing external lookups, and exposing standard REST endpoints.

## Requirements

### Requirement: Retrieve top five heaviest Pokémon
The system SHALL expose an HTTP GET endpoint at `/api/v1/pokemon/heaviest` that returns the top 5 heaviest Pokémon sorted in descending order of weight.

#### Scenario: Successfully retrieve top 5 heaviest Pokémon
- **WHEN** a client sends a GET request to `/api/v1/pokemon/heaviest`
- **THEN** the system returns HTTP 200 with a JSON array containing up to 5 Pokémon sorted in descending order by weight

#### Scenario: External data service is unreachable
- **WHEN** a client sends a GET request to `/api/v1/pokemon/heaviest` and the external Pokémon service fails or is unreachable
- **THEN** the system returns HTTP 502 or 503 with an RFC 7807 compliant problem details response

### Requirement: Retrieve top five highest Pokémon
The system SHALL expose an HTTP GET endpoint at `/api/v1/pokemon/highest` that returns the top 5 highest Pokémon sorted in descending order of height.

#### Scenario: Successfully retrieve top 5 highest Pokémon
- **WHEN** a client sends a GET request to `/api/v1/pokemon/highest`
- **THEN** the system returns HTTP 200 with a JSON array containing up to 5 Pokémon sorted in descending order by height

#### Scenario: External service returns non-success response
- **WHEN** a client sends a GET request to `/api/v1/pokemon/highest` and the upstream Pokémon service returns an error
- **THEN** the system returns an RFC 7807 problem details response indicating upstream service failure

### Requirement: Retrieve top five Pokémon with highest base experience
The system SHALL expose an HTTP GET endpoint at `/api/v1/pokemon/experience` that returns the top 5 Pokémon sorted in descending order of base experience.

#### Scenario: Successfully retrieve top 5 Pokémon with highest base experience
- **WHEN** a client sends a GET request to `/api/v1/pokemon/experience`
- **THEN** the system returns HTTP 200 with a JSON array containing up to 5 Pokémon sorted in descending order by base experience

### Requirement: Standardized Pokémon ranking representation
The system SHALL return each Pokémon in ranking responses with its name, numerical identifier, weight, height, and base experience.

#### Scenario: Pokémon data formatting
- **WHEN** any ranking endpoint produces a successful response
- **THEN** every element in the JSON array contains `id`, `name`, `weight`, `height`, and `baseExperience` fields

### Requirement: Caching external Pokémon responses
The system SHALL cache retrieved Pokémon ranking or resource data to prevent redundant network requests and avoid third-party rate-limiting.

#### Scenario: Serving subsequent queries from cache
- **WHEN** repeated requests are made for the same ranking criteria within the cache validity period
- **THEN** the system returns the ranking without re-invoking the external Pokémon data provider

### Requirement: RFC 7807 global error representation
The system SHALL return structured RFC 7807 Problem Details for all client and server error conditions.

#### Scenario: Unhandled or external service errors
- **WHEN** an exception occurs during request processing
- **THEN** the response body matches application/problem+json containing status, title, detail, and timestamp

