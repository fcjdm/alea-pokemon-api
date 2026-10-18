# Proposal: Unify Pokémon Ranking Endpoint

## Why

Clients currently need to use three different URLs to request the same Pokémon ranking representation. A single endpoint with a validated ranking criterion simplifies the API and makes ranking selection consistent.

## What Changes

- Replace the heaviest, highest, and experience routes with `GET /api/v1/pokemon?sortBy=...`.
- Support `weight`, `height`, and `experience` values.
- Return an RFC 7807 `400 Bad Request` when `sortBy` is unsupported.
- Update the OpenAPI contract, tests, and bilingual README examples.

## Impact

The three existing ranking URLs are removed. Clients use the unified endpoint and must provide the `sortBy` query parameter.
