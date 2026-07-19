# Online Store API

Backend take-home assessment for Code81 - a small online store API built with Java 21, Spring Boot, and PostgreSQL.

> **Status:** Backend complete (catalog, auth, customers, cart & orders, staff/admin, activity logging, Stripe payment simulation). Angular frontend and Docker Compose are in progress and will be added before the final review - this README will be updated again at that point.

## Tech stack

- Java 21, Spring Boot 3.5
- PostgreSQL, Flyway (versioned schema migrations - see `src/main/resources/db/migration`)
- Spring Security + JWT (access + refresh tokens)
- Stripe (Checkout Sessions, test mode) for payment simulation
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, AssertJ

## Running it locally

1. Create a PostgreSQL database, e.g. `online_store` (via pgAdmin4 or `createdb`).
2. Copy the config template and fill in your own values:
   ```bash
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```
   Edit `application.yml` with your local DB credentials, a JWT secret, and (optionally, only needed for the payment flow) Stripe test keys. This file is gitignored on purpose - it's never committed.
3. Run: `mvn spring-boot:run`
4. Flyway creates the full schema and sample data automatically on startup (see `db/migration/` - each file is a small, reviewable step: catalog, then auth, then customers, then orders, then staff/logging, then payments).
5. API docs: `http://localhost:8080/swagger-ui.html`

A Postman collection covering every endpoint is included at `postman/online-store-full.postman_collection.json` - import it, run the "Login" requests first (they auto-save your access/refresh tokens), then explore from there.

## Seed accounts

| Role | Email | Password |
|---|---|---|
| Admin (staff) | `admin@code81.local` | `Admin@123` |
| Demo customer | `jane.doe@example.com` | `Customer@123` |

## Data model

Category → Product, Customer → Address / Cart / Orders, StaffUser → ActivityLog, Orders → OrderItem / Payment. Full ERD: `docs/erd.png` *(add your draw.io export here before final submission)*.

## Key design decisions

- **Two separate user tables** (`Customer`, `StaffUser`) rather than one polymorphic table - they have genuinely different fields, permissions, and login flows, and mixing them would mean every query filtering by type.
- **JWT access tokens (15 min) + rotated, DB-stored refresh tokens (7 days).** Refresh tokens are opaque UUIDs, not JWTs, specifically so a stolen or logged-out token can be revoked immediately instead of waiting out its expiry - see `refresh_token` table.
- **Checkout uses pessimistic row locks** (`SELECT ... FOR UPDATE`, always acquired in ascending product-id order) so stock can never go negative under concurrent orders, and two simultaneous checkouts sharing a product can't deadlock each other.
- **Order status is an explicit state machine** (`PLACED → PAID/CANCELLED → SHIPPED → DELIVERED`) - invalid transitions are rejected with 409, not silently allowed.
- **Products are soft-deleted** (`active = false`) since past orders reference them by foreign key; categories are hard-deleted, but blocked (409) while any product still references them.
- **Activity logging** records every staff write (catalog changes, staff account changes, order status changes) with who/what/when, viewable at `GET /api/activity-logs` (admin only).
- **Payments use Stripe Checkout Sessions**, confirmed via a signature-verified webhook rather than trusting the browser redirect - see "Testing Stripe locally" below.

## API surface

Full detail is in Swagger UI; summary:

| Area | Base path | Notes |
|---|---|---|
| Categories / Products | `/api/categories`, `/api/products` | Public reads, staff-only writes |
| Auth | `/api/auth/**` | Separate customer/staff login, refresh, logout |
| Customers | `/api/customers/**` | Public registration, self-service profile |
| Addresses | `/api/customers/me/addresses/**` | Customer-scoped |
| Cart | `/api/cart/**` | Customer-scoped |
| Orders | `/api/orders/**` | Place/view/cancel (customer), manage (staff) |
| Staff | `/api/staff/**` | Admin only |
| Activity Logs | `/api/activity-logs` | Admin only, read-only |
| Payments | `/api/orders/{id}/payment/checkout-session`, `/api/payments/webhook` | Customer starts checkout; Stripe confirms via webhook |

## Testing Stripe locally

Payments run in Stripe **test mode** - no real money moves.

1. Install the [Stripe CLI](https://stripe.com/docs/stripe-cli) and run `stripe login`.
2. With the app running, in a second terminal: `stripe listen --forward-to localhost:8080/api/payments/webhook`. Copy the `whsec_...` value it prints into your local `application.yml` as the webhook secret, then restart the app.
3. Place an order, call `POST /api/orders/{id}/payment/checkout-session`, open the returned URL in a browser, and pay with card `4242 4242 4242 4242` (any future expiry, any CVC).
4. The webhook marks the order `PAID` automatically - confirm with `GET /api/orders/{id}`.

## Assumptions

- Prices are in USD.
- One shipping address per order (no split shipments).
- No cart/order limits or promo codes - out of scope for this assessment.
- `success_url`/`cancel_url` for Stripe are currently placeholder API endpoints; these will point at real Angular pages once the frontend is added.

## Running tests

```
mvn test
```

## What's next

Angular 19 frontend (main customer + staff flows), then Docker Compose to bring up the whole stack (app + Postgres + frontend) with one command. This README will be revised once those land.
