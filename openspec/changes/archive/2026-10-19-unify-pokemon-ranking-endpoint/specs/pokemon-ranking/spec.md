## MODIFIED Requirements

### Requirement: Retrieve top five Pokémon by selected ranking criterion
The system SHALL expose a single HTTP GET endpoint at `/api/v1/pokemon` that requires a `sortBy` query parameter. Supported values are `weight`, `height`, and `experience`; the endpoint returns the top 5 Pokémon sorted in descending order by the selected attribute.

#### Scenario: Successfully retrieve a ranking
- **WHEN** a client sends a GET request with `sortBy` set to `weight`, `height`, or `experience`
- **THEN** the system returns HTTP 200 with a JSON array containing up to 5 Pokémon sorted by the corresponding attribute

#### Scenario: Unsupported ranking criterion
- **WHEN** a client sends a GET request with an unsupported `sortBy` value
- **THEN** the system returns HTTP 400 with an RFC 7807 Problem Details response identifying the supported values

#### Scenario: External data service is unavailable
- **WHEN** a client requests a ranking and the external Pokémon service fails or is unreachable
- **THEN** the system returns HTTP 502 or 503 with an RFC 7807 compliant Problem Details response

### Requirement: Standardized Pokémon ranking representation
The system SHALL return each Pokémon in ranking responses with its name, numerical identifier, weight, height, and base experience.

#### Scenario: Pokémon data formatting
- **WHEN** the unified ranking endpoint produces a successful response
- **THEN** every element in the JSON array contains `id`, `name`, `weight`, `height`, and `baseExperience` fields

### Requirement: Caching external Pokémon responses
The system SHALL cache retrieved Pokémon ranking or resource data to prevent redundant network requests and avoid third-party rate-limiting.

#### Scenario: Serving subsequent queries from cache
- **WHEN** repeated requests are made for the same ranking criteria within the cache validity period
- **THEN** the system returns the ranking without re-invoking the external Pokémon data provider
