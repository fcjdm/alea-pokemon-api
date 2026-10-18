# Design: Unified Pokémon Ranking Endpoint

The OpenAPI contract defines one GET operation at `/api/v1/pokemon` and a required `sortBy` query parameter constrained to `weight`, `height`, or `experience`. The OpenAPI generator maps this enum to the application `RankingCriteria` type, so the controller accepts a typed criterion directly and delegates to the ranking service without conversion logic. A Spring converter binds the contract's lowercase query values; unsupported values are rejected as an RFC 7807 `400 Bad Request`.

Ranking strategies, data retrieval, response structure, and upstream error behavior remain unchanged.
