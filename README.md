# 🍽️ Restaurante API

API REST para gestão de restaurante, desenvolvida com **Java 21** e **Spring Boot**, responsável pelo cadastro de produtos do cardápio, pelo controle de pedidos por mesa e pelo fluxo de preparo dos itens na cozinha.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-6DB33F?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?logo=flyway)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven)

---

## 📑 Sumário

- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Modelo de dados](#-modelo-de-dados)
- [Regras de negócio](#-regras-de-negócio)
- [Endpoints](#-endpoints)
- [Como executar](#-como-executar)
- [Variáveis de ambiente](#-variáveis-de-ambiente)
- [Documentação (Swagger)](#-documentação-swagger)

---

## 🚀 Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 4.1.2 | Framework base |
| Spring Web MVC | Camada REST |
| Spring Data JPA | Persistência |
| Spring Validation | Validação |
| Spring Cloud OpenFeign | Cliente HTTP declarativo |
| PostgreSQL | Banco de dados |
| Flyway | Versionamento do banco (migrations) |
| springdoc-openapi 3.2.0 | Documentação Swagger UI |
| Lombok | Redução de boilerplate |
| Maven (wrapper) | Build e dependências |

---

## 🏗️ Arquitetura

Projeto organizado em camadas:

```
src/main/java/com/sistema/restaurante
├── controller/        # Endpoints REST (Produto, Pedido, Cozinha)
├── service/           # Regras de negócio
├── repository/        # Interfaces Spring Data JPA
├── domain/
│   ├── entity/        # Entidades JPA
│   └── enums/         # Enums de status e forma de pagamento
├── dto/               # Records de Request/Response
├── exception/         # Exceções e resposta padronizada de erro
└── RestauranteApplication.java

src/main/resources
├── application.properties
└── db/migration/
    ├── V1__create_restaurante_schema.sql   # Criação das tabelas
    └── V2__insert_dados_iniciais.sql       # Dados iniciais
```

---

## 🗄️ Modelo de dados

| Tabela | Descrição |
|---|---|
| `mesas` | Mesas do restaurante (número, capacidade, status) |
| `categorias_produtos` | Categorias do cardápio |
| `produtos` | Itens do cardápio (preço, disponibilidade, tempo de preparo) |
| `pedidos` | Pedido vinculado a uma mesa |
| `pedido_itens` | Itens de cada pedido (quantidade, preço unitário, status) |
| `pagamentos` | Pagamentos de um pedido |
| `fechamento_conta` | Fechamento da conta (subtotal, taxa de serviço, desconto, total) |

### Enums

| Enum | Valores |
|---|---|
| `StatusMesa` | `LIVRE`, `OCUPADA`, `RESERVADA`, `INATIVA` |
| `StatusPedido` | `ABERTO`, `EM_PREPARO`, `PRONTO`, `ENTREGUE`, `FECHADO`, `CANCELADO` |
| `StatusItemPedido` | `PENDENTE`, `EM_PREPARO`, `PRONTO`, `ENTREGUE`, `CANCELADO` |
| `StatusPagamento` | `PENDENTE`, `APROVADO`, `RECUSADO`, `CANCELADO` |
| `FormaPagamento` | `DINHEIRO`, `CARTAO_CREDITO`, `CARTAO_DEBITO`, `PIX` |

### Dados iniciais (migration V2)

- **5 mesas** (números 1 a 5)
- **4 categorias:** Entradas, Pratos Principais, Bebidas, Sobremesas
- **5 produtos:** Batata Frita, X-Burger Artesanal, Filé com Fritas, Suco Natural, Pudim

---

## 📋 Regras de negócio

**Pedidos**
- Só é possível abrir pedido para uma mesa existente e com status `LIVRE`.
- Ao abrir o pedido, ele inicia como `ABERTO` e a mesa passa para `OCUPADA`.
- Itens só podem ser adicionados a pedidos com status `ABERTO`.
- O produto precisa existir e estar disponível no cardápio.
- A quantidade deve ser maior que zero.
- O preço unitário do item é copiado do preço atual do produto; o item inicia como `PENDENTE`.
- O total do item é calculado como `precoUnitario × quantidade`.

**Cozinha**
- Os itens seguem o fluxo `PENDENTE` → `EM_PREPARO` → `PRONTO` → `ENTREGUE`.
- Só itens `PENDENTE` podem iniciar o preparo.
- Só itens `EM_PREPARO` podem ser marcados como prontos.
- Só itens `PRONTO` podem ser entregues.
- As listas de itens pendentes e em preparo são ordenadas por ID (ordem de chegada).

**Produtos**
- O produto deve pertencer a uma categoria existente.
- Se `disponivel` não for informado, o produto é cadastrado como disponível.

---

## 🔗 Endpoints

### Produtos — `/produtos`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/produtos` | Cadastra um produto |
| `GET` | `/produtos` | Lista produtos (paginado, padrão: 10 por página, ordenado por `nome`) |
| `GET` | `/produtos/{id}` | Busca produto por ID |
| `PUT` | `/produtos/{id}` | Atualiza um produto |
| `DELETE` | `/produtos/{id}` | Exclui um produto |

<details>
<summary>Exemplo de requisição</summary>

```json
{
  "categoriaId": 2,
  "nome": "Parmegiana",
  "descricao": "Filé à parmegiana com arroz e fritas",
  "preco": 54.90,
  "disponivel": true,
  "tempoPreparoMinutos": 25
}
```
</details>

### Pedidos — `/pedidos`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/pedidos` | Abre um pedido para uma mesa |
| `GET` | `/pedidos` | Lista pedidos (paginado) |
| `GET` | `/pedidos/{id}` | Busca pedido por ID |
| `POST` | `/pedidos/{pedidoId}/itens` | Adiciona item ao pedido |
| `GET` | `/pedidos/{pedidoId}/itens` | Lista itens do pedido |

<details>
<summary>Exemplos de requisição</summary>

**Abrir pedido**
```json
{
  "mesaId": 1,
  "observacao": "Cliente aniversariante"
}
```

**Adicionar item**
```json
{
  "produtoId": 2,
  "quantidade": 2,
  "observacao": "Sem cebola"
}
```
</details>

### Cozinha — `/cozinha`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/cozinha/itens-pendentes` | Lista itens com status `PENDENTE` |
| `GET` | `/cozinha/itens-em-preparo` | Lista itens com status `EM_PREPARO` |
| `PATCH` | `/cozinha/itens/{itemId}/iniciar-preparo` | Muda o item de `PENDENTE` para `EM_PREPARO` |
| `PATCH` | `/cozinha/itens/{itemId}/marcar-pronto` | Muda o item de `EM_PREPARO` para `PRONTO` |
| `PATCH` | `/cozinha/itens/{itemId}/entregar` | Muda o item de `PRONTO` para `ENTREGUE` |

<details>
<summary>Exemplo de resposta</summary>

```json
{
  "itemId": 1,
  "pedidoId": 1,
  "numeroMesa": 1,
  "produtoNome": "X-Burger Artesanal",
  "quantidade": 2,
  "observacao": "Sem cebola",
  "precoUnitario": 34.90,
  "status": "EM_PREPARO"
}
```
</details>

### Formato de erro

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "erro": "Erro de regra de negócio",
  "mensagens": ["Mesa não está livre"]
}
```

---

## ▶️ Como executar

### Pré-requisitos

- Java 21+
- PostgreSQL em execução

### Passo a passo

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd restaurante

# 2. Crie o banco de dados
psql -U postgres -c "CREATE DATABASE restaurante;"

# 3. Configure as variáveis de ambiente
cp .env.example .env
# edite o .env com suas credenciais

# 4. Rode a aplicação
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

As migrations do Flyway criam as tabelas e inserem os dados iniciais automaticamente na primeira execução.

A API sobe em `http://localhost:8080`.

---

## 🔐 Variáveis de ambiente

O arquivo `.env` na raiz do projeto é carregado automaticamente (`spring.config.import`). Ele **não é versionado** — use o `.env.example` como modelo.

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/restaurante` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `JPA_SHOW_SQL` | Exibe SQL no console | `true` |

---

## 📖 Documentação (Swagger)

Com a aplicação rodando, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
