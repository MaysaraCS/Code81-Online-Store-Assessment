# Online Store - Code81 Assessment

A full-stack online store: Java 21 / Spring Boot backend, Angular 19 frontend, PostgreSQL, JWT auth, Stripe payment simulation.

here is the link for my ERD : https://drive.google.com/file/d/1GDceZDP5mM_BDBtqEC7ofdjKDL_aDBFu/view?usp=sharing OPEN IT USING draw.io

## Screenshots

**Login**

![Login page](./screenshots/login-page.png)

**Checkout**
![Checkout page](./screenshots/checkout.png)

**Home (post-login landing page)**
![Home page](./screenshots/home-page.png)

**Address management**
![Address page](./screenshots/address.png)

**Payment**
![Payment page](./screenshots/payment.png)

**Admin**
![Admin page](./screenshots/admin.png)

## Tech stack

| Layer        | Tech                                                                                    |
| ------------ | --------------------------------------------------------------------------------------- |
| Backend      | Java 21, Spring Boot 3.5, Spring Security + JWT, PostgreSQL, Flyway, Stripe (test mode) |
| Frontend     | Angular 19, standalone components, Reactive Forms, signals                              |
| Docs/testing | Swagger UI, Postman collection, draw.io ERD                                             |

## Repo structure

```
.
├── README.md                        <- you are here
├── docker-compose.yml                <- run the whole stack with one command
├── .env.example                       <- copy to .env and fill in secrets
├── screenshots/                       <- project pictures
├── Postman/                            <- Postman collection
├── online-store-api/                  <- backend (Spring Boot)
│   └── README.md
└── code81_online_store_angular/       <- frontend (Angular)
└── README.md
```

## Running it with Docker (recommended - one command for everything)

```bash
cp .env.example .env
# edit .env with a real JWT secret and your Stripe test keys

docker compose up --build
```

This starts Postgres, the backend (Flyway runs all migrations automatically), and the frontend (served via nginx) together, networked to talk to each other. Open `http://localhost:4200` once it's up.

Stop with `docker compose down` (add `-v` to also wipe the database and start fully fresh next time). See `online-store-api/README.md` for seed account credentials and Stripe webhook testing steps - those are unchanged whether you run natively or via Docker.

## Running it locally (without Docker)

You need both the backend and frontend running at the same time - the frontend expects the API at `http://localhost:8080`.

### 1. Backend

```bash
cd online-store-api
cp src/main/resources/application.yml.example src/main/resources/application.yml
# edit application.yml with your local Postgres credentials, a JWT secret,
# and (only needed for the payment flow) Stripe test keys
mvn spring-boot:run
```

Runs on `http://localhost:8080`. Flyway creates the schema and seed data automatically on first startup.

### 2. Frontend

```bash
cd code81_online_store_angular
npm install
ng serve
```

Runs on `http://localhost:4200`.

### 3. Try it

Open `http://localhost:4200` - you'll land on the login page. Use either the seeded admin account or the seeded demo customer account (documented in `online-store-api/README.md`) to sign in.

## API docs & testing tools

- **Swagger UI:** `http://localhost:8080/swagger-ui.html` (backend must be running)
- **Postman collection:** `Postman/online-store-full.postman_collection.json`
- **ERD:** see the Google Drive link at the top of this README

## Status

Backend: complete (catalog, auth, customers, cart & orders, staff/admin, activity logging, Stripe payment simulation).
Frontend: complete (catalog, cart, checkout, orders, staff/admin screens, Stripe payment flow).
Docker Compose: complete - full stack runs with one command.
