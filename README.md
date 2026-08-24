# tpi-backend-bff

BFF Spring Boot 3 / Java 17 que centraliza los servicios de usuarios y productos.

## Endpoints

- `POST /api/v1/auth/login` (credentials in JSON body)
- `GET /api/v1/auth/session`
- `POST /api/v1/auth/logout` (requires the CSRF cookie and `X-CSRF-Token` header)
- `GET /api/v1/usuarios` (authenticated session)
- `GET /api/v1/usuarios/{id}` (authenticated session)
- `GET /api/v1/productos` (authenticated session)
- `GET /api/v1/overview` (authenticated session)
- `GET /health`

## Puerto

- Local: `8080`

## Variables de entorno

- `USERS_SERVICE_URL` (default: `http://localhost:8081`)
- `PRODUCTS_SERVICE_URL` (default: `http://localhost:8082`)
- `DEMO_LOGIN_EMAIL` (required; injected by environment or secret manager)
- `DEMO_LOGIN_PASSWORD` (required; injected by environment or secret manager)
- `REDIS_URL` (default: `redis://localhost:6379`)

The session is stored in Redis and exposed to the browser only as an opaque,
`HttpOnly`, `Secure`, `SameSite=Strict` `__Host-tpi-session` cookie. The CSRF
token is bound to the server session and is not an authentication token.

## Ejecutar local

```bash
mvn spring-boot:run
```

## Ejecutar tests

```bash
mvn test
```

## Docker

```bash
docker build -t tpi-backend-bff .
docker run --rm -p 8080:8080 -e USERS_SERVICE_URL=http://host.docker.internal:8081 -e PRODUCTS_SERVICE_URL=http://host.docker.internal:8082 tpi-backend-bff
```
