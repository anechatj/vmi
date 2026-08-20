# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

VMI — a personal portfolio project simulating a voluntary motor insurance (ประกันภัยรถยนต์ภาคสมัครใจ) policy recording system. Monorepo: React frontend (`apps/web`) + Spring Boot backend (`apps/policy-api`), modular monolith (ADR-0001), local dev via Docker Desktop (Postgres, MinIO, Redis, Keycloak).

Architecture decisions live in `docs/adr/`; operational procedures (setup, credential changes, troubleshooting) live in `docs/runbooks/` — check both before assuming a convention.

## Commands

### Infra (Docker)
```bash
cp infra/docker/.env.example infra/docker/.env   # first time only; .env is gitignored, machine-specific
make up / make down / make restart / make logs / make ps / make clean
```
`make` targets just wrap `docker compose -f infra/docker/docker-compose.local.yml --env-file infra/docker/.env <cmd>`. On Windows, `make` may resolve to a stray legacy Borland `make.exe` instead of GNU Make if one is on `PATH` — if `make up` fails oddly, run the `docker compose` command directly instead of debugging the Makefile.

Keycloak realm/client/roles auto-import from `infra/keycloak/vmi-realm.json` on every `docker compose up` (`--import-realm`) — don't recreate them by hand. Keycloak users are **not** included in that export (partial-export limitation) and must be recreated per machine — see `docs/runbooks/keycloak-setup.md` / `new-machine-setup.md`.

### apps/web (run from `apps/web/`)
```bash
npm install
npm run dev          # localhost:5173
npm run build         # typecheck + vite build
npm run test          # Vitest — single file: npx vitest run src/path/File.test.tsx
npm run test:e2e     # Playwright (auto-starts dev server per playwright.config.ts)
npm run lint          # Oxlint
npm run typecheck
npm run format         # Prettier --write
```

### apps/policy-api (run from `apps/policy-api/`)
```bash
./mvnw test                              # unit tests only — fast, no Docker required
./mvnw verify                            # unit + integration tests — REQUIRED for *IT.java, needs Docker Desktop
./mvnw test -Dtest=CoverageTypeServiceTest
./mvnw verify -Dit.test=CoverageTypeControllerIT
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run   # runs against real docker-compose infra, localhost:8081
```
**`./mvnw test` silently skips every `*IT.java` file** — Surefire's default include pattern doesn't match `*IT`, and only the `maven-failsafe-plugin` (bound to `./mvnw verify`) runs them. This is intentional (keeps the fast unit-test loop fast) but easy to forget when adding a new integration test.

API docs: `http://localhost:8081/swagger-ui.html` (has a working Authorize button — paste a JWT, see Auth below) or raw spec at `/v3/api-docs`.

### Root-level
```bash
bash scripts/generate-api-client.sh   # or: make generate-client
```
Generates the TS client into `packages/api-client/generated` from policy-api's live OpenAPI spec — policy-api must be running first. Output isn't committed; regenerate after any API change.

## Architecture

### apps/policy-api package layout (ADR-0001)
```
com.vmi.policyapi/
├─ master/{feature}/   # reference data — one vertical slice per feature; coveragetype is the reference implementation to copy
├─ policy/ document/ payment/ reporting/   # planned core-domain packages, not yet implemented
└─ common/              # SecurityConfig, CacheConfig, GlobalExceptionHandler, ErrorResponse — shared infra only
```
Packages must not reach into another package's internals (entity/repository) directly — cross-package calls go through a package's public `Service`. This is what would let a package split into its own microservice later without a rewrite.

Each feature vertical slice (`master/coveragetype/` is the template) follows: package-private JPA `Entity` + `Repository` → MapStruct `Mapper` (record DTOs ↔ entity) → `Service` (`@Transactional`; `@Cacheable` only on single-resource `get(id)`, deliberately **not** on paginated lists — `Page<T>` doesn't round-trip cleanly through Redis JSON serialization) → `Controller` under `/api/v1/...` (reads open to any authenticated user, writes gated with `@PreAuthorize("hasRole('admin')")`) → exceptions handled centrally by `common/exception/GlobalExceptionHandler`, which maps them to the shared `ErrorResponse` record (used by both apps for a consistent error contract).

### Auth
Keycloak realm `vmi`, client `vmi-web` (public client, Authorization Code Flow + PKCE from the client side, though the Keycloak client isn't yet configured to *require* it — see `apps/web/README.md`). `apps/policy-api`'s `SecurityConfig` validates JWTs via `jwk-set-uri` (deliberately not `issuer-uri` — the latter does a synchronous `/.well-known/openid-configuration` fetch at bean-startup time and fails app boot if Keycloak isn't up yet; `jwk-set-uri` fetches lazily on first token validation) and maps the `realm_access.roles` claim to Spring `ROLE_*` authorities via a custom `JwtAuthenticationConverter` (Keycloak doesn't put roles where Spring Security expects them by default).

To call a protected endpoint manually: get a token via `POST {keycloak}/realms/vmi/protocol/openid-connect/token` with `grant_type=password`, `client_id=vmi-web`, and a Keycloak user's credentials (direct access grants are enabled on that client for exactly this).

### Spring Boot 4 / Jackson 3 specifics
This backend runs Spring Boot 4.1, which postdates most training data — several things differ from "classic" Spring Boot 3 conventions and aren't discoverable except by hitting them:
- Jackson 3 (`tools.jackson`) is the framework default now; `com.fasterxml.jackson.databind.ObjectMapper` (Jackson 2) has no auto-configured Spring bean anymore. Don't `@Autowired` it — instantiate directly (`new ObjectMapper()`) where still needed (e.g. serializing request bodies in tests).
- `@DynamicPropertySource` is only honored on the test class itself or a base class it extends — never on a class that's merely `@Import`-ed (e.g. a shared `TestcontainersConfiguration`). Put it on `AbstractIntegrationTest` and extend that.
- A `src/test/resources/application.yaml` **shadows** `src/main/resources/application.yaml` entirely rather than merging with it — prefer `@DynamicPropertySource` for test-only config over adding a same-named test resource file.
- `spring.jpa.open-in-view` is explicitly disabled in `application.yaml` — keep it that way (default-on hides N+1 queries).
- A broad `@ExceptionHandler(Exception.class)` in `GlobalExceptionHandler` will intercept `AuthorizationDeniedException` before Spring Security's own filter chain gets a chance to turn it into a 403 — it needs its own explicit handler above the catch-all, or `@PreAuthorize` denials come back as 500s.

### Frontend structure
Feature-based, not type-based: `src/features/{domain}/{api,components,types.ts}` — each feature owns its TanStack Query hooks and presentational components. `src/pages` stays thin (composes features + layout only). `src/components/ui` is only for components already duplicated across 2+ features — don't pre-extract.

### Local credentials
Keycloak Admin Console, the Keycloak `admin` application user (realm `vmi`), and the PostgreSQL role are unified to `admin` / `Test1234!` across dev machines for memorability — see `docs/runbooks/change-local-credentials.md`. The Postgres role and the Keycloak user sharing the literal name `admin` is intentional, not a naming collision bug; they're unrelated systems (`apps/policy-api` → Postgres vs. browser login → Keycloak). `infra/docker/.env` is gitignored and per-machine; `.env.example` reflects current expected defaults.
