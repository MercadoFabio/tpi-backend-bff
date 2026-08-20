# tpi-backend-bff

BFF Spring Boot 3 / Java 17 que centraliza los servicios de usuarios y productos.

## Endpoints

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `GET /api/productos`
- `GET /api/overview`
- `GET /health`

## Puerto

- Local: `8080`

## Variables de entorno

- `USERS_SERVICE_URL` (default: `http://localhost:8081`)
- `PRODUCTS_SERVICE_URL` (default: `http://localhost:8082`)
- `ALLOWED_ORIGINS` (default: `http://localhost:4200,http://localhost:4300,http://localhost`)

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
