# 🍊 S04.02 - Fruit & Provider API (Level 2: MySQL & Docker Compose)

A production-ready REST API built with Spring Boot for managing fruit inventory with provider relationships. This is the second level of a three-level exercise, upgrading from H2 to MySQL with a `@ManyToOne` JPA relationship between Fruit and Provider entities. The application runs in a multi-container Docker Compose environment and uses environment-based database configuration with no hardcoded credentials.

## 📋 What This Project Does

This API manages a fruit inventory system where each fruit is linked to a provider (supplier). You can register providers with their name and country, then create fruits associated with those providers. The API supports full CRUD operations for both entities, filtering fruits by provider, and enforces business rules like preventing deletion of providers that have associated fruits and rejecting duplicate provider names.

The response for each fruit includes the full nested provider object — no second API call needed. All input is validated, errors return structured JSON through a centralized GlobalExceptionHandler, and the application runs alongside MySQL via Docker Compose.

## 🧠 What I Learned Building This

Building on Level 1's foundation (DTOs, validation, Docker), this level introduced relational database concepts, multi-container orchestration, and more complex business logic:

- **JPA Relationships (`@ManyToOne`)**: Each Fruit holds a reference to its Provider via `@ManyToOne @JoinColumn(name = "provider_id", nullable = false)`. This maps to a foreign key constraint in MySQL — the database itself enforces that every fruit must belong to a valid provider.
- **Nested DTO Responses**: `FruitResponseDto` includes a full `ProviderResponseDto` object instead of just a `providerId`. This eliminates the N+1 API call problem — the client gets all the data it needs in a single request.
- **Business Rule Enforcement**: Providers with associated fruits cannot be deleted (returns 400). Duplicate provider names are rejected (returns 409). These rules exist in the service layer, not the database — giving us control over error messages and HTTP status codes.
- **Docker Compose Multi-Container Setup**: The application and MySQL run in separate containers on the same Docker network. The app connects to MySQL using the service name (`mysql`) as the hostname — Docker's internal DNS resolves it automatically.
- **Environment-Based Configuration**: Database credentials are injected via environment variables stored in a `.env` file (excluded from Git). The `application.properties` uses `${DB_HOST}`, `${DB_USERNAME}`, `${DB_PASSWORD}` with no defaults — the application fails to start if they're missing, which is the secure default.
- **H2 for Test Isolation**: Tests run against H2 in-memory instead of MySQL, using a separate `application-test.properties` with `@ActiveProfiles("test")`. This means anyone can clone the project and run tests immediately without Docker or MySQL installed.
- **Custom Query Methods**: `existsByName()` and `existsByNameAndIdNot()` in the Provider repository — Spring Data JPA generates the SQL from the method name. `existsByNameAndIdNot` is used during updates to check for duplicate names while excluding the provider being updated.
- **Provider-Aware Fruit Filtering**: `GET /fruits?providerId=1` filters fruits by provider. The controller uses `@RequestParam(required = false)` to make it optional — same endpoint serves both "get all" and "get by provider" without code duplication.

## 🛠️ Technologies

- Java 21 (Temurin LTS)
- Spring Boot 4.0.6
- Spring Data JPA + MySQL 8.0
- H2 Database (test scope)
- Maven (wrapper included — no local Maven installation needed)
- Bean Validation (Hibernate Validator)
- Lombok
- JUnit 5 + MockMvc
- Docker + Docker Compose
- IntelliJ IDEA

## 📁 Project Structure

The project follows a layered MVC architecture with clear separation of concerns:

```
src/main/java/cat/itacademy/s04/t02/n02/fruit_api_mysql/
│
├── controller/                       # Receives HTTP requests, returns ResponseEntity
│   ├── FruitController.java          # CRUD + filter by provider: /fruits
│   └── ProviderController.java       # CRUD: /providers
│
├── dto/                              # Data Transfer Objects — API contract layer
│   ├── FruitRequestDto.java          # Input: providerId + name + weightInKilos
│   ├── FruitResponseDto.java         # Output: id + name + weightInKilos + nested provider
│   ├── ProviderRequestDto.java       # Input: name + country
│   └── ProviderResponseDto.java      # Output: id + name + country
│
├── exception/                        # Centralized error handling
│   ├── FruitNotFoundException.java           # 404 — fruit ID doesn't exist
│   ├── ProviderNotFoundException.java        # 404 — provider ID doesn't exist
│   ├── ProviderAlreadyExistsException.java   # 409 — duplicate provider name
│   ├── ProviderHasFruitsException.java       # 400 — can't delete provider with fruits
│   └── GlobalExceptionHandler.java           # @RestControllerAdvice — formats all errors
│
├── mapper/                           # Entity ↔ DTO conversion utilities
│   ├── FruitMapper.java              # Requires Provider entity for toEntity conversion
│   └── ProviderMapper.java           # Static methods with null-safety
│
├── model/                            # JPA persistence entities
│   ├── Fruit.java                    # @ManyToOne → Provider, custom constructor
│   └── Provider.java                 # @Entity with unique name constraint
│
├── repository/                       # Data access layer
│   ├── FruitRepository.java          # + findByProviderId(Long)
│   └── ProviderRepository.java       # + existsByName(), existsByNameAndIdNot()
│
├── service/                          # Business logic layer
│   ├── FruitService.java             # Interface
│   ├── FruitServiceImpl.java         # Validates provider exists before creating fruit
│   ├── ProviderService.java          # Interface
│   └── ProviderServiceImpl.java      # Duplicate name check, deletion protection
│
└── FruitApiMysqlApplication.java     # Spring Boot entry point
```

```
src/test/java/cat/itacademy/s04/t02/n02/fruit_api_mysql/
│
├── FruitControllerIntegrationTest.java     # Full-stack fruit endpoint tests (6 tests)
├── ProviderControllerIntegrationTest.java  # Full-stack provider endpoint tests (8 tests)
└── FruitApiMysqlApplicationTests.java      # Context load test
```

## 🔌 API Endpoints

### Provider Management

| Method | Endpoint | Description | Success | Error |
|--------|----------|-------------|---------|-------|
| POST | `/providers` | Create a new provider | 201 Created | 400 / 409 Conflict |
| GET | `/providers` | Retrieve all providers | 200 OK | — |
| GET | `/providers/{id}` | Retrieve a provider by ID | 200 OK | 404 Not Found |
| PUT | `/providers/{id}` | Update a provider | 200 OK | 404 / 400 / 409 |
| DELETE | `/providers/{id}` | Remove a provider (no fruits) | 204 No Content | 404 / 400 |

### Fruit Management

| Method | Endpoint | Description | Success | Error |
|--------|----------|-------------|---------|-------|
| POST | `/fruits` | Create a fruit linked to a provider | 201 Created | 400 / 404 |
| GET | `/fruits` | Retrieve all fruits | 200 OK | — |
| GET | `/fruits?providerId={id}` | Filter fruits by provider | 200 OK | 404 |
| GET | `/fruits/{id}` | Retrieve a fruit by ID | 200 OK | 404 Not Found |
| PUT | `/fruits/{id}` | Update a fruit | 200 OK | 404 / 400 |
| DELETE | `/fruits/{id}` | Remove a fruit | 204 No Content | 404 Not Found |

### Error Response Format

All errors return a consistent JSON structure:

**404 Not Found:**
```json
{
    "timestamp": "2026-04-29T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Fruit with ID: 999 not found"
}
```

**409 Conflict (Duplicate Provider):**
```json
{
    "timestamp": "2026-04-29T12:00:00",
    "status": 409,
    "error": "Conflict",
    "message": "Provider with name 'FruitCorp' already exists"
}
```

**400 Validation Error:**
```json
{
    "timestamp": "2026-04-29T12:00:00",
    "status": 400,
    "error": "Validation Failed",
    "errors": {
        "name": "Fruit name cannot be empty",
        "weightInKilos": "Weight must be greater than zero"
    }
}
```

## ⚙️ How to Run

### Option A: Docker Compose (Recommended)

1. Copy the environment template and fill in your values:
```bash
cp .env.example .env
```

2. Start the application:
```bash
docker-compose up --build
```

This starts both MySQL and the Spring Boot application. The API will be available at `http://localhost:9000`.

To stop:
```bash
docker-compose down
```

### Option B: Local Execution (requires MySQL running)

Set the required environment variables:
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=fruit_db
export DB_USERNAME=your_user
export DB_PASSWORD=your_password
```

Then run:
```bash
./mvnw spring-boot:run
```

### Run Tests (no Docker or MySQL needed)

```bash
./mvnw test
```

Tests use H2 in-memory — no external database required.

## 💻 API Usage Examples

### Create a provider

```bash
curl -X POST http://localhost:9000/providers \
  -H "Content-Type: application/json" \
  -d '{"name": "FruitCorp", "country": "Spain"}'
```

Response (201 Created):
```json
{
    "id": 1,
    "name": "FruitCorp",
    "country": "Spain"
}
```

### Create a fruit linked to a provider

```bash
curl -X POST http://localhost:9000/fruits \
  -H "Content-Type: application/json" \
  -d '{"providerId": 1, "name": "Tangerine", "weightInKilos": 3}'
```

Response (201 Created):
```json
{
    "id": 1,
    "name": "Tangerine",
    "weightInKilos": 3,
    "provider": {
        "id": 1,
        "name": "FruitCorp",
        "country": "Spain"
    }
}
```

### Get all fruits

```bash
curl http://localhost:9000/fruits
```

### Filter fruits by provider

```bash
curl http://localhost:9000/fruits?providerId=1
```

### Update a provider

```bash
curl -X PUT http://localhost:9000/providers/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "FruitCorp International", "country": "Portugal"}'
```

### Delete a provider (fails if it has fruits)

```bash
curl -X DELETE http://localhost:9000/providers/1
```

Response: 400 Bad Request (if fruits exist) or 204 No Content (if no fruits)

### Try creating a fruit with invalid provider

```bash
curl -X POST http://localhost:9000/fruits \
  -H "Content-Type: application/json" \
  -d '{"providerId": 999, "name": "Ghost Fruit", "weightInKilos": 1}'
```

Response (404 Not Found):
```json
{
    "timestamp": "2026-04-29T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Provider with ID: 999 not found"
}
```

## 🧪 Test Coverage

| Metric | Count |
|--------|-------|
| Total Tests | 15 (0 failures) |
| Provider Integration Tests | 8 |
| Fruit Integration Tests | 6 |
| Context Load Test | 1 |

### Tests by Layer

**🔗 Provider Integration Tests (ProviderControllerIntegrationTest — 8 tests):**
- Create provider with valid data returns 201, get all providers returns list, get by valid ID returns 200, update with valid data returns 200, delete provider with no fruits returns 204 and confirms removal, duplicate provider name returns 409 Conflict, delete provider with associated fruits returns 400 Bad Request, get non-existing ID returns 404

**🔗 Fruit Integration Tests (FruitControllerIntegrationTest — 6 tests):**
- Create fruit linked to valid provider returns 201, get all fruits returns list, get by valid ID returns 200, update fruit name and weight returns 200, delete fruit returns 204 and confirms removal, create fruit with invalid provider ID returns 404

## 🏗️ Architecture & Design Decisions

- **Nested DTO over flat response**: `FruitResponseDto` embeds the full `ProviderResponseDto` instead of returning just a `providerId`. This eliminates extra API calls and gives the client everything it needs in one request. The trade-off is slightly larger payloads, but for this domain the provider data is always relevant when viewing a fruit.
- **No default credentials**: `application.properties` uses `${DB_USERNAME}` and `${DB_PASSWORD}` without defaults. If the environment variables are missing, the app fails to start. This is intentional — silently connecting with default passwords is a security antipattern.
- **`.env` for secrets management**: Database credentials live in a `.env` file excluded from version control via `.gitignore`. A `.env.example` is committed to show the required variables without exposing real values. Docker Compose reads `.env` automatically — no hardcoded passwords in any committed file.
- **Service-level business rules**: Duplicate name detection and deletion protection live in the service layer, not via database constraints alone. This gives us control over HTTP status codes (409 vs 400) and human-readable error messages.
- **`existsByNameAndIdNot()` for updates**: When updating a provider, we need to check if the new name conflicts with *other* providers, not with itself. This query method excludes the current provider's ID from the duplicate check.
- **H2 for tests, MySQL for production**: Tests run against H2 in-memory with `@ActiveProfiles("test")` and a separate `application-test.properties`. This means tests are fast (no Docker needed), isolated (`create-drop` schema), and portable — anyone can clone and run tests immediately.
- **`@Transactional` on integration tests**: Each test runs in a transaction that rolls back after execution. No test data leaks between tests. Each test starts with a clean database.
- **Docker Compose with healthcheck**: The MySQL service includes a healthcheck (`mysqladmin ping`). The app service uses `depends_on` with `condition: service_healthy` to ensure MySQL is fully ready before the app starts — not just that the container exists, but that MySQL can accept connections.
- **Non-root Docker container**: Same as Level 1 — the production image runs as `springuser` following the Principle of Least Privilege.

## 🐳 Docker Compose & Environment Configuration

Credentials are stored in a `.env` file (excluded from Git via `.gitignore`). Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

**`.env.example`** (committed — shows structure without real values):
```
DB_HOST=mysql
DB_PORT=3306
DB_NAME=your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password
MYSQL_ROOT_PASSWORD=your_root_password
```

**`docker-compose.yml`** references these variables:
```yaml
services:
  mysql:
    image: mysql:8.0
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    container_name: mysql_fruit_api       # Fixed container name for easy identification
    restart: always                        # Auto-restart on crash or reboot
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}           # Auto-creates this database on startup
      MYSQL_USER: ${DB_USERNAME}           # Application-level user (not root)
      MYSQL_PASSWORD: ${DB_PASSWORD}       # Application-level password
    ports:
      - "3307:3306"                        # Host 3307 → Container 3306 (avoids local MySQL conflict)
    volumes:
      - mysql_data:/var/lib/mysql          # Persist data between container restarts
    networks:
      - fruit_network

  app:
    build: .                               # Build from Dockerfile in project root
    container_name: spring_fruit_api       # Fixed container name
    ports:
      - "9000:9000"
    environment:
      DB_HOST: ${DB_HOST}                  # Service name = hostname inside Docker network
      DB_PORT: ${DB_PORT}                  # Container's internal port (not 3307)
      DB_NAME: ${DB_NAME}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      mysql:
        condition: service_healthy         # Wait for MySQL healthcheck to pass
    networks:
      - fruit_network

networks:
  fruit_network:
    driver: bridge                         # Default network driver for container communication

volumes:
  mysql_data:                              # Named volume for data persistence
```

## 📈 Potential Improvements

- `weightInKilos` could be `double` instead of `int` for more precise measurements (e.g., 2.5 kg). Noted as a design constraint from the exercise specification.
- Add Mockito unit tests for the service layer to test business logic in isolation.
- Add pagination for `GET /fruits` and `GET /providers` using Spring Data's `Pageable`.
- Add `spring.jpa.open-in-view=false` to disable the open-in-view antipattern.
- Testcontainers integration for testing against a real MySQL instance (blocked by Docker Desktop + Windows named pipe compatibility — documented for future resolution).
- Integration with Swagger/OpenAPI for interactive API documentation.

## 📈 Roadmap

- **Level 1** ✅: H2 in-memory database, single entity, Dockerfile, 18 tests.
- **Level 2** ✅: MySQL with Docker Compose, `@ManyToOne` relationship, 15 tests.
- **Level 3**: MongoDB persistence with embedded subdocuments for managing fruit orders.