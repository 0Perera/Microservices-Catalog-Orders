# Microservices Catalog & Orders

Projeto pessoal feito com apoio de IA para uma atividade de curso, com o objetivo de exercitar na prática os conceitos teóricos de microsserviços: service discovery, API gateway, comunicação entre serviços e roteamento dinâmico.

Stack: Spring Boot 3.3.1 + Spring Cloud 2023.0.3, Java 21, Maven (multi-módulo).

## O que cada serviço faz

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `eureka-server` | 8761 | Registro de serviços (service discovery). Cada serviço se cadastra aqui ao subir, dizendo "eu existo e estou neste host/porta". |
| `products-service` | 8100 | CRUD de produtos. Persiste em banco H2 em memória. |
| `orders-service` | 8200 | Cria e lista pedidos. Não tem banco de dados — guarda os pedidos em memória (`ConcurrentHashMap`), então eles se perdem ao reiniciar o serviço. |
| `api-gateway` | 8700 | Porta de entrada única da aplicação. Recebe todas as requisições externas, valida um token simples e redireciona para o serviço certo. |

## Como eles se conversam

```
cliente (curl/Postman)
       │
       ▼
  api-gateway (8700)  ──── valida token Bearer
       │
       ├── /products/** ──► products-service (8100) ──► H2 (productsdb)
       │
       └── /orders/**   ──► orders-service (8200)
                                  │
                                  └── chama products-service via Feign
                                      (para pegar nome/preço atual do produto)

Todos os serviços (exceto o gateway na hora de rotear) se registram
no eureka-server (8761), que funciona como uma "lista telefônica":
o gateway não sabe o endereço fixo de products-service ou orders-service,
ele pergunta ao Eureka "quem é products-service agora?" e é
redirecionado (load balancing via lb://).
```

**Por que o `orders-service` chama o `products-service`?** Para criar um pedido, o cliente manda só `productId` e `quantity` — não manda nome nem preço. O `orders-service` busca essas informações em tempo real no catálogo (via Feign), em vez de confiar no que o cliente mandaria, evitando que alguém manipule o preço de um item na requisição.

**Por que a segurança fica só no gateway?** É a única porta de entrada pública. `products-service` e `orders-service` não têm autenticação própria — eles confiam que só o gateway fala com eles. Isso simplifica a demo, mas significa que se alguém acessar `products-service` ou `orders-service` diretamente (sem passar pelo gateway), não há nenhuma trava.

## Simplificações propositais (é uma demo de estudo, não produção)

- **Token fixo no código** (`demo-token-123`, em `BearerTokenFilter`): não expira, não tem usuário/escopo, está hardcoded. Em um sistema real seria JWT/OAuth2.
- **Pedidos só em memória**: reiniciar `orders-service` apaga todos os pedidos.
- **Sem tratamento de erro refinado**: buscar um produto ou pedido inexistente retorna erro 500 em vez de 404 (não há um handler de exceções customizado).
- **Sem testes automatizados.**

## Build

```bash
mvn -q -DskipTests package
```

## Ordem para subir

A ordem importa porque cada serviço se registra no Eureka e o gateway só consegue rotear depois que `products-service`/`orders-service` aparecem no registro:

1. `eureka-server`
2. `products-service`
3. `orders-service`
4. `api-gateway`

Para rodar um módulo individualmente:
```bash
mvn -pl products-service spring-boot:run
```

Acesse o Eureka (mostra quais serviços estão registrados e ativos): http://localhost:8761

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
curl -i -H "Authorization: $TOKEN" -H "Content-Type: application/json" \
 -d '{"name":"Headset","price":250.00,"description":"USB"}' \
 http://localhost:8700/products
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
curl -i -H "Authorization: $TOKEN" -H "Content-Type: application/json" \
 -d '{"items":[{"productId":1,"quantity":2},{"productId":2,"quantity":1}]}' \
 http://localhost:8700/orders
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