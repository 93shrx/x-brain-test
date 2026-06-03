# x-brain-test

[![CI](https://github.com/93shrx/x-brain-test/actions/workflows/ci.yml/badge.svg)](https://github.com/93shrx/x-brain-test/actions/workflows/ci.yml)

> 🇧🇷 [Português](#português) · 🇺🇸 [English](#english)

---

## Português

API REST para gerenciamento de pedidos desenvolvida com Spring Boot 4.0.6, H2, RabbitMQ e Docker.

### Como executar

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15672` (usuário: `guest` / senha: `guest`)
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:xbraindb`)

## Observação

O projeto utiliza Spring Boot 4.x e H2 Database.

Devido a uma incompatibilidade conhecida entre Spring Boot 4.x e o H2 Console, a interface web do H2 pode não estar disponível.

A persistência pode ser validada normalmente através dos endpoints da API e dos logs SQL gerados pelo Hibernate.

### Endpoints

#### Criar pedido
```http
POST /api/orders
Content-Type: application/json

{
  "customerCode": "CUST-001",
  "productCodes": ["PROD-A", "PROD-B"],
  "totalAmount": 150.00,
  "deliveryAddress": "Rua das Flores, 123"
}
```

Resposta (`201 Created`):
```json
{
  "id": 1,
  "customerCode": "CUST-001",
  "productCodes": ["PROD-A", "PROD-B"],
  "totalAmount": 150.00,
  "deliveryAddress": "Rua das Flores, 123",
  "status": "PUBLISHED",
  "createdAt": "2026-06-03T10:00:00"
}
```

#### Buscar pedido
```http
GET /api/orders/{id}
```

#### Health check
```http
GET /actuator/health
```

### Arquitetura

```
Client
  │
  ▼
POST /api/orders
  │
  ▼
OrderController
  │
  ▼
OrderService ──────────────► OrderPublisher
  │                                │
  ▼                                ▼
OrderRepository               RabbitMQ (orders.queue)
(H2 - persiste pedido)             │
                                   ▼
                           DeliveryConsumer
                                   │
                                   ▼
                           DeliveryRepository
                           (H2 - persiste entrega)
```

### Banco de dados

O projeto utiliza H2 em memória conforme solicitado no desafio. Os dados são recriados a cada inicialização da aplicação. Não há necessidade de instalar ou executar um banco externo.

O console do H2 fica disponível em `http://localhost:8080/h2-console` com a JDBC URL `jdbc:h2:mem:xbraindb`.

### Solução

**Fluxo:**

1. `POST /api/orders` recebe a requisição, valida os campos e persiste o pedido com status `CREATED`
2. O pedido é publicado no RabbitMQ (`orders.exchange` → `orders.queue`) via `OrderPublisher`
3. O status é atualizado para `PUBLISHED`
4. O `DeliveryConsumer` consome a mensagem da fila e persiste uma `Delivery` com o ID do pedido e o endereço de entrega

**Decisões técnicas:**

| Decisão | Motivo |
|---|---|
| Spring Boot 4.0.6 + Java 21 | Versão mais recente, aproveitando `Stream.toList()` e APIs modernas |
| H2 in-memory | Banco relacional em memória conforme requisito, sem dependência externa |
| `ddl-auto=create` | Evita erros no pool de conexões durante shutdown que `create-drop` pode causar |
| `Jackson2JsonMessageConverter` como bean | Spring Boot detecta automaticamente e injeta no `RabbitTemplate` sem configuração manual |
| ProblemDetail (RFC 7807) | Padrão moderno de resposta de erro HTTP, nativo no Spring 6+ |
| `depends_on` com healthcheck | Garante que o RabbitMQ esteja pronto antes de a aplicação subir |

### Integração Contínua

A pipeline GitHub Actions executa em todo push e pull request para `main`:

- Build Maven (`mvn clean verify`)
- Testes automatizados com RabbitMQ real (service container)
- Validação do Dockerfile

Um container RabbitMQ é levantado automaticamente no CI antes dos testes, garantindo que o contexto da aplicação sobe e conecta corretamente à fila.

### Diferenciais implementados

- Pipeline de CI com GitHub Actions
- Spring Boot Actuator expondo `health`, `info` e `metrics`
- Logging estruturado com SLF4J em todas as camadas
- Tratamento de erros centralizado com `@RestControllerAdvice`

### O que faria com mais tempo

- **Testes de integração** com Testcontainers — RabbitMQ e H2 reais nos testes, sem mocks
- **Dead Letter Queue** para mensagens que falham no consumo, evitando perda silenciosa
- **Endpoint de busca com Elasticsearch** conforme diferencial do desafio
- **Flyway** para versionamento de migrações de banco
- **Retry no consumidor** com `@Retryable` para falhas transitórias
- **OpenAPI/Swagger** para documentação interativa da API
- **Publisher Confirms** do RabbitMQ para garantia de entrega na publicação

---

## English

REST API for order management built with Spring Boot 4.0.6, H2, RabbitMQ and Docker.

### How to run

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15672` (user: `guest` / password: `guest`)
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:xbraindb`)

### Endpoints

#### Create order
```http
POST /api/orders
Content-Type: application/json

{
  "customerCode": "CUST-001",
  "productCodes": ["PROD-A", "PROD-B"],
  "totalAmount": 150.00,
  "deliveryAddress": "123 Flower Street"
}
```

Response (`201 Created`):
```json
{
  "id": 1,
  "customerCode": "CUST-001",
  "productCodes": ["PROD-A", "PROD-B"],
  "totalAmount": 150.00,
  "deliveryAddress": "123 Flower Street",
  "status": "PUBLISHED",
  "createdAt": "2026-06-03T10:00:00"
}
```

#### Get order
```http
GET /api/orders/{id}
```

#### Health check
```http
GET /actuator/health
```

### Architecture

```
Client
  │
  ▼
POST /api/orders
  │
  ▼
OrderController
  │
  ▼
OrderService ──────────────► OrderPublisher
  │                                │
  ▼                                ▼
OrderRepository               RabbitMQ (orders.queue)
(H2 - persists order)              │
                                   ▼
                           DeliveryConsumer
                                   │
                                   ▼
                           DeliveryRepository
                           (H2 - persists delivery)
```

### Database

The project uses an H2 in-memory database as required. Data is recreated on every application startup — no external database installation needed.

The H2 console is available at `http://localhost:8080/h2-console` using the JDBC URL `jdbc:h2:mem:xbraindb`.

### Solution

**Flow:**

1. `POST /api/orders` receives the request, validates the fields and persists the order with status `CREATED`
2. The order is published to RabbitMQ (`orders.exchange` → `orders.queue`) via `OrderPublisher`
3. The status is updated to `PUBLISHED`
4. `DeliveryConsumer` reads the message from the queue and persists a `Delivery` with the order ID and delivery address

**Technical decisions:**

| Decision | Reason |
|---|---|
| Spring Boot 4.0.6 + Java 21 | Latest version, leveraging `Stream.toList()` and modern APIs |
| H2 in-memory | In-memory relational database as required, no external dependency |
| `ddl-auto=create` | Avoids connection pool errors on shutdown that `create-drop` can cause |
| `Jackson2JsonMessageConverter` as bean | Spring Boot auto-detects it and injects into `RabbitTemplate` automatically |
| ProblemDetail (RFC 7807) | Modern HTTP error response standard, native in Spring 6+ |
| `depends_on` with healthcheck | Ensures RabbitMQ is ready before the application starts |

### Continuous Integration

The GitHub Actions pipeline runs on every push and pull request to `main`:

- Maven build (`mvn clean verify`)
- Automated tests with a real RabbitMQ service container
- Dockerfile validation

A RabbitMQ container is automatically started in CI before the tests run, ensuring the application context loads and connects to the queue correctly.

### Implemented extras

- CI pipeline with GitHub Actions
- Spring Boot Actuator exposing `health`, `info` and `metrics`
- Structured logging with SLF4J across all layers
- Centralized error handling with `@RestControllerAdvice`

### What I would improve with more time

- **Integration tests** with Testcontainers — real RabbitMQ and H2 in tests, no mocks
- **Dead Letter Queue** for messages that fail consumption, avoiding silent data loss
- **Elasticsearch search endpoint** as mentioned in the challenge extras
- **Flyway** for database migration versioning
- **Consumer retry** with `@Retryable` for transient failures
- **OpenAPI/Swagger** for interactive API documentation
- **Publisher Confirms** in RabbitMQ for guaranteed message delivery
