# FoodMarket – Marketplace

A full-stack web application where **suppliers** publish offers for food ingredients and **buyers** (e.g. restaurants) browse the catalogue, build a cart and place orders. An **admin** role manages users and monitors activity.

- **Backend:** Spring Boot REST API with stateless **JWT** authentication and role-based access control, PostgreSQL via Spring Data JPA.
- **Frontend:** Angular single-page application (`front/`).

## Features

**Buyer**
- Register / log in, browse the public catalogue of offers
- Cart and order creation (stock availability is checked and reduced on order)
- Order history, cancel an order or confirm delivery
- Review suppliers after an order

**Supplier**
- Dashboard with own offers: create, edit, activate / deactivate, delete
- Offers have price, available quantity, minimum order quantity and a validity period
- Company profile (name, address, tax ID, description, rating)

**Admin**
- User statistics (orders and total spent per buyer, offers and total sales per supplier)
- Activate / deactivate users
- Read buyers' reviews of any supplier

## Architecture

```
Angular (localhost:4200)  ──HTTP/JSON + JWT──►  Spring Boot (localhost:8080)  ──JPA──►  PostgreSQL
```

Backend layers: `controller → service → repository`, with DTOs and mappers separating the API from JPA entities. Domain model: `User`, `Role`, `Supplier`, `Category`, `Ingredient`, `Offer`, `Order`, `OrderItem`, `Review`. Order lifecycle: `PENDING → CONFIRMED → SHIPPED → DELIVERED` (or `CANCELLED`).

### REST API overview

| Prefix | Access | Purpose |
|---|---|---|
| `/api/auth` | public | register, login, logout |
| `/api/categories`, `/api/ingredients`, `/api/offers`, `/api/suppliers` | public | catalogue and supplier data |
| `/api/users`, `/api/orders`, `/api/reviews` | authenticated | profile, orders, reviews |
| `/api/admin` | `ADMIN` only | statistics, user activation, review moderation |

## Getting started

### Prerequisites

- JDK 18+
- Node.js and npm (for Angular 21)
- PostgreSQL

### 1. Database

Create an empty database and the three roles used by the application:

```sql
CREATE DATABASE "FoodMarket";
```

Start the backend once (tables are created automatically by Hibernate), then insert the roles:

```sql
INSERT INTO roles (name, description) VALUES
  ('BUYER',    'Buys ingredients'),
  ('SUPPLIER', 'Sells ingredients'),
  ('ADMIN',    'Administrator');
```

New users are registered as `BUYER`; to try the supplier or admin views, change the user's role in the database.

### 2. Backend

Check the connection settings and JWT secret in `src/main/resources/application.yaml`, then:

```bash
./mvnw spring-boot:run
```

The API runs on http://localhost:8080.

### 3. Frontend

```bash
cd front
npm install
npm start
```

Open http://localhost:4200.

## Tech stack

**Backend:** Java, Spring Boot 4, Spring Security, Spring Data JPA / Hibernate, JWT (jjwt), PostgreSQL, Lombok, Maven
**Frontend:** Angular 21, TypeScript, RxJS
