# Dire Gebeya 🛒

A full-stack e-commerce platform with a customer storefront, an admin dashboard, and a REST API backend.

## Project Structure

```
.
├── Diregebeya frontend/        # Customer storefront (Next.js)
└── backend/
    ├── diregebeya-backend/     # REST API (Spring Boot + PostgreSQL)
    ├── diregebeya-admin/       # Admin dashboard (Next.js)
    ├── diregebeya-web/         # Web client (Next.js)
    └── docker-compose.yml      # Local dev environment
```

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.5
- Spring Security with JWT authentication (access + refresh tokens)
- Spring Data JPA + PostgreSQL 16
- MapStruct & Lombok
- Springdoc OpenAPI (Swagger UI)
- Docker & Docker Compose

**Frontend / Admin**
- Next.js 16 (App Router)
- React 19
- TypeScript
- Tailwind CSS 4

## Features

**Customer**
- User registration and login (JWT)
- Browse products with search, filtering, and categories
- Shopping cart
- Checkout and order tracking
- Wishlist
- Product reviews and ratings
- Coupon codes at checkout

**Admin**
- Dashboard with sales and order statistics
- Product and category management (with product images)
- Order management and status updates
- User management (roles, status)
- Coupon management

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose

### 1. Run the backend with Docker (recommended)

```bash
cd backend
docker compose up --build
```

This starts:
- **PostgreSQL 16** on port `5433`
- **Backend API** on port `8090` (mapped to container port 8080)

### 2. Run the backend manually (without Docker)

```bash
cd backend/diregebeya-backend
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080` by default.

### 3. Run the admin dashboard

```bash
cd backend/diregebeya-admin
npm install
npm run dev
```

Open `http://localhost:3000`.

### 4. Run the customer frontend

```bash
cd "Diregebeya frontend"
npm install
npm run dev
```

## API Documentation

Once the backend is running, Swagger UI is available at:

```
http://localhost:8090/swagger-ui.html
```

OpenAPI spec: `http://localhost:8090/v3/api-docs`

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://postgres:5432/diregebeya` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Secret for signing JWTs (change in production!) | dev-only value |
| `JWT_EXPIRATION_MS` | Access token lifetime | `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh token lifetime | `604800000` (7d) |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins | `http://localhost:5173,http://localhost:3000` |
| `SERVER_PORT` | Backend port | `8080` |

⚠️ **Never commit real secrets.** Set `JWT_SECRET` and database credentials via environment variables in production.

## Backend Architecture

The backend follows a layered architecture:

```
controller → service → repository → entity
     ↓          ↓
    dto      mapper (MapStruct)
```

- **Security**: JWT filter, custom `UserDetailsService`, role-based access (`ADMIN`, `STAFF`, `CUSTOMER`)
- **Seeders**: default roles, admin user, and sample products are created on first run
- **Error handling**: global exception handler with consistent error responses

## Author

**Natnael (naty-nati)** — [GitHub](https://github.com/naty-nati)

## License

This project is for educational and portfolio purposes.
