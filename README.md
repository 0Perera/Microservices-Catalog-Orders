# Microservices Catalog & Orders (Spring Boot + Spring Cloud)

Módulos:
- `eureka-server` (8761)
- `products-service` (8100, H2, CRUD)
- `orders-service` (8200, simulação/Feign)
- `api-gateway` (8700, rotas + token Bearer `demo-token-123`)

## Build
```bash
mvn -q -DskipTests package
```

## Ordem para subir
1. `eureka-server`
2. `products-service`
3. `orders-service`
4. `api-gateway`

Acesse o Eureka: http://localhost:8761

## Testes manuais (via curl)

### 1) Via Gateway sem token (bloqueio)
```bash
curl -i http://localhost:8700/products
# Esperado: 401
```

### 2) Via Gateway com token (libera)
```bash
TOKEN="Bearer demo-token-123"
curl -i -H "Authorization: $TOKEN" http://localhost:8700/products
```

### 3) Criar produto (via Gateway)
```bash
curl -i -H "Authorization: $TOKEN" -H "Content-Type: application/json"  -d '{"name":"Headset","price":250.00,"description":"USB"}'  http://localhost:8700/products
```

### 4) Listar produtos
```bash
curl -s -H "Authorization: $TOKEN" http://localhost:8700/products | jq
```

### 5) Buscar produto por ID
```bash
curl -s -H "Authorization: $TOKEN" http://localhost:8700/products/1 | jq
```

### 6) Criar pedido (usa preços do catálogo)
```bash
curl -i -H "Authorization: $TOKEN" -H "Content-Type: application/json"  -d '{"items":[{"productId":1,"quantity":2},{"productId":2,"quantity":1}]}'  http://localhost:8700/orders
```

### 7) Listar pedidos
```bash
curl -s -H "Authorization: $TOKEN" http://localhost:8700/orders | jq
```

### 8) Buscar pedido por ID (use o ID retornado na criação)
```bash
ORDER_ID="COLE_AQUI"
curl -s -H "Authorization: $TOKEN" http://localhost:8700/orders/$ORDER_ID | jq
```

## H2 Console (products-service)
http://localhost:8100/h2-console  
JDBC URL: `jdbc:h2:mem:productsdb`, user: `sa`, password: (em branco)

## Observações
- Armazenamento de pedidos é **in-memory**.
- Segurança aplicada **apenas no Gateway** via `BearerTokenFilter`.
- `data.sql` cria 3 produtos iniciais.
