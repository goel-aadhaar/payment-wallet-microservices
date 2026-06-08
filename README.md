# Payment Wallet Application

A production-grade Payment Wallet platform: a **Spring Boot 4 / Java 25 microservices backend**
(PostgreSQL, Kafka, JWT) behind a central API gateway, with a **Next.js / Tailwind CSS frontend**.

## 🏗️ Architecture

A shared **`common`** library (auto-configured into every service) provides cross-cutting concerns:
RFC-7807 `ProblemDetail` error handling, a request correlation-id filter, a `PageResponse` envelope,
and the shared `JwtUtil`.

| Service | Port | Responsibility | DB |
|---|---|---|---|
| **api-gateway** | 8080 | Routing, JWT validation, CORS, rate limiting (Bucket4j) | — |
| **user-service** | 8081 | Registration, login (issues JWT), profile, change-password | `userdb` |
| **wallet-service** | 8088 | Balances, credit/debit, holds (hold → capture/release), statement | `walletdb` |
| **transaction-service** | 8082 | Transfers (coordinates wallet via resilient Feign), history/search | `transactiondb` |
| **notification-service** | 8083 | Read/unread notifications from transaction events (Kafka consumer) | `notificationdb` |
| **reward-service** | 8084 | Cashback points + loyalty tier from transaction events (Kafka consumer) | `rewarddb` |
| **frontend** | 3000 | Next.js dashboard, transactions, rewards | — |

**Flow:** a transfer holds funds on the sender, verifies the receiver, captures (debits sender) and
credits the receiver, then publishes a `txn-initiated` Kafka event. Reward and notification services
consume it (idempotently) to award points and notify both parties.

## 🛠️ Tech Stack

**Backend:** Java 25 · Spring Boot 4.0.6 · **PostgreSQL + Flyway** migrations · Spring Data JPA ·
Spring Kafka (with dead-letter topics) · **Resilience4j** (circuit breaker / timeouts) on inter-service
calls · **Actuator + Micrometer/Prometheus** · JWT · Springdoc OpenAPI · JUnit 5 + Mockito.

**Frontend:** Next.js (App Router) · TypeScript · Tailwind CSS v4 · Axios.

---

## 🚀 Running locally

### Prerequisites
- **Docker Desktop** (runs PostgreSQL + Kafka), **JDK 25**, **Node.js 22+**.

### Option A — `run-local.ps1` (Windows/PowerShell, recommended for dev)
Runs PostgreSQL + Kafka in Docker and the services/frontend natively on the host:

```powershell
.\run-local.ps1                 # build, start infra + all 6 services + frontend
.\run-local.ps1 -SkipBuild      # reuse existing jars
.\run-local.ps1 -SkipFrontend   # backend only (lighter on RAM)
.\stop-local.ps1                # stop everything (Postgres data is preserved)
```

> Each service JVM is capped (`-Xmx384m`) and started staggered, because Spring Boot otherwise
> defaults max heap to ~25% of RAM and 6 simultaneous JVMs can exhaust memory on small machines.

### Option B — Docker Compose (fully containerized)
```bash
docker compose build
docker compose up -d
```
Frontend → http://localhost:3000 · API Gateway → http://localhost:8080.
(Containers emit **structured JSON logs** via `LOGGING_STRUCTURED_FORMAT_CONSOLE=ecs`.)

Configuration is via environment variables — see [`.env.example`](.env.example).

---

## 📡 Key API endpoints (via the gateway, `:8080`)

**Auth / users**
- `POST /auth/signup`, `POST /auth/login`, `POST /auth/change-password`
- `GET /api/v1/users/{id}`, `PUT /api/v1/users/{id}` (profile)

**Wallet**
- `GET /api/v1/wallets/user/{userId}`, `POST /api/v1/wallets/credit`
- `GET /api/v1/wallets/user/{userId}/transactions` (paginated statement)

**Transactions**
- `POST /api/v1/transactions`, `GET /api/v1/transactions/{id}`
- `GET /api/v1/transactions/search?userId=&status=&page=&size=`

**Rewards**
- `GET /api/v1/rewards/user/{userId}`, `GET /api/v1/rewards/user/{userId}/summary`

**Notifications**
- `GET /api/v1/notifications/user/{userId}?unreadOnly=&page=&size=`
- `GET /api/v1/notifications/user/{userId}/unread-count`
- `PATCH /api/v1/notifications/{id}/read`, `PATCH /api/v1/notifications/user/{userId}/read-all`
- `DELETE /api/v1/notifications/{id}`

Validation failures and domain errors return **RFC-7807 `application/problem+json`** with a
`correlationId`. Interactive Swagger UI is available per service, e.g.
http://localhost:8081/swagger-ui.html.

## 📈 Observability
- Health (with liveness/readiness): `GET /actuator/health` on every service.
- Prometheus metrics: `GET /actuator/prometheus`.
- Every log line carries `[correlationId,traceId,spanId]`; the `X-Correlation-Id` header is propagated.

## ✅ Testing & CI
- `mvn verify` runs unit tests (JUnit 5 + Mockito) and context checks against the `test` profile
  (in-memory H2, Flyway off) — no Docker required.
- GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)) builds + tests the backend
  and lints/builds the frontend on every push/PR.

## 🗄️ Database
PostgreSQL, one database per service, schema owned by **Flyway** (`src/main/resources/db/migration`)
with `ddl-auto=validate`. Data persists across restarts in the `pgdata` Docker volume.
