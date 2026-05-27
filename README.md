# ⚖️ Sistema de Gestão de Processos Judiciais

### Teste Técnico — Attus Procuradoria Digital

Sistema full stack para gestão de processos judiciais desenvolvido como resposta ao desafio técnico da Attus Procuradoria Digital.

A aplicação cobre o ciclo completo de um processo judicial:

- cadastro;
- acompanhamento;
- atualização de status;
- auditoria de alterações;
- rastreabilidade operacional.

O domínio foi modelado considerando o contexto real de procuradorias públicas, alinhado ao produto da Attus, especializado em execução fiscal e contencioso judicial para estados e municípios.

---

## 📑 Sumário

- [1. Visão Geral](#1--visão-geral)
- [2. Tecnologias Utilizadas](#2--tecnologias-utilizadas)
- [3. Arquitetura do Projeto](#3--arquitetura-do-projeto)
- [4. Pré-requisitos](#4--pré-requisitos)
- [5. Como Executar](#5--como-executar)
- [6. Endpoints da API](#6--endpoints-da-api)
- [7. Funcionalidades](#7--funcionalidades)
- [8. Boas Práticas Aplicadas](#8--boas-práticas-aplicadas)
- [9. Decisões Técnicas e Trade-offs](#9--decisões-técnicas-e-trade-offs)
- [10. Melhorias Futuras](#10--melhorias-futuras)
- [11. Testes](#11--testes)
- [12. Autor](#12--autor)

---

# 1. 📖 Visão Geral

O sistema foi projetado para refletir cenários reais de uma procuradoria digital, onde:

- procuradores lidam com alto volume de processos;
- alterações precisam ser auditáveis;
- prazos processuais são críticos;
- confiabilidade e rastreabilidade são obrigatórias.

A solução foi construída com foco em:

- arquitetura limpa;
- separação de responsabilidades;
- escalabilidade;
- observabilidade;
- qualidade de engenharia.

---

# 2. 🧰 Tecnologias Utilizadas

## Back-end

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Spring Boot | 3.5.14 |
| Spring Data JPA | Hibernate 6.6 |
| PostgreSQL | 17 |
| Maven | 3.9 |
| Lombok | 1.18 |
| SLF4J + Logback | Logging |
| JUnit 5 | Testes |
| Mockito | Mocking |
| AssertJ | Assertions |

---

## Front-end

| Tecnologia | Versão |
|---|---|
| React | 19 |
| Vite | 8 |
| Material UI (MUI) | 5 |
| Axios | HTTP Client |
| React Router DOM | 6 |

---

# 3. 🏗 Arquitetura do Projeto

O repositório foi organizado como **monorepo**, contendo:

- aplicação back-end;
- aplicação front-end;
- documentação técnica.

---

## 📂 Estrutura de Diretórios

```text
Attus/
├── processojudicial/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/attus/processojudicial/
│   │   │   │   ├── controller/
│   │   │   │   ├── domain/
│   │   │   │   │   └── enums/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       └── application.yaml
│   │   └── test/
│   │       └── java/com/attus/processojudicial/
│   │           └── service/
│   ├── INCIDENT.md
│   ├── README.md
│   └── pom.xml
│
└── frontend/
    └── src/
        ├── pages/
        ├── services/
        └── utils/
```

---

## 🔄 Fluxo de Requisição

```text
Cliente (React)
     |
     | HTTP Request
     v
ProcessoController
     |
     v
ProcessoService
     |
     v
ProcessoRepository
     |
     v
PostgreSQL
     |
     v
ProcessoService
     |
     v
ProcessoController
     |
     | HTTP Response (JSON)
     v
Cliente (React)
```

---

## 📌 Responsabilidades por Camada

| Camada | Responsabilidade |
|---|---|
| Controller | Recebe requisições e retorna respostas HTTP |
| Service | Regras de negócio e orquestração |
| Repository | Acesso ao banco |
| DTO | Contrato de entrada/saída |
| Domain | Modelagem de entidades |

---

# 4. ⚙️ Pré-requisitos

- Java 25+
- Node.js 20 LTS+
- PostgreSQL 17+
- Maven 3.9+
- Git

---

# 5. 🚀 Como Executar

---

## 5.1 Clone o repositório

```bash
git clone https://github.com/CauaHvS/processojudicial.git

cd processojudicial
```

---

## 5.2 Configure o banco de dados

```sql
CREATE DATABASE processo_judicial;
```

---

## 5.3 Configure variáveis de ambiente

O `application.yaml` utiliza fallback para desenvolvimento local:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/processo_judicial
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:suasenha}
```

### Variáveis disponíveis

| Variável | Descrição |
|---|---|
| DB_USERNAME | Usuário PostgreSQL |
| DB_PASSWORD | Senha PostgreSQL |

---

## 5.4 Executar o Back-end

```bash
cd processojudicial

./mvnw spring-boot:run
```

### API disponível em

```text
http://localhost:8080
```

---

## 5.5 Executar o Front-end

```bash
cd frontend

npm install

npm run dev
```

### Front-end disponível em

```text
http://localhost:5173
```

---

# 6. 🌐 Endpoints da API

---

## 📂 Processos

| Método | Endpoint | Descrição | Retorno |
|---|---|---|---|
| GET | `/api/processos` | Lista todos os processos | 200 |
| GET | `/api/processos/{id}` | Busca por ID | 200 / 404 |
| GET | `/api/processos/status/{status}` | Filtra por status | 200 |
| GET | `/api/processos/prazo-vencendo` | Processos próximos do vencimento | 200 |
| POST | `/api/processos` | Cria novo processo | 201 / 409 |
| PUT | `/api/processos/{id}` | Atualização completa | 200 / 404 |
| PATCH | `/api/processos/{id}/status` | Atualiza status | 200 / 404 |
| DELETE | `/api/processos/{id}` | Remove processo | 204 |
| GET | `/api/processos/{id}/logs` | Histórico de alterações | 200 |

---

## 📌 Status Disponíveis

```text
EM_ANDAMENTO
AGUARDANDO_PRAZO
SUSPENSO
ENCERRADO
```

---

## 📥 Exemplo — Criar Processo

### Request

```http
POST /api/processos
```

```json
{
  "numero": "0001/2026",
  "titulo": "Execução Fiscal - Município de BH",
  "tipo": "Execução Fiscal",
  "descricao": "Cobrança de IPTU referente ao exercício 2025",
  "status": "EM_ANDAMENTO",
  "responsavel": "Dr. João Silva",
  "prazo": "2026-12-31"
}
```

---

## 📥 Exemplo — Atualizar Status

### Request

```http
PATCH /api/processos/{id}/status
```

```json
{
  "status": "AGUARDANDO_PRAZO",
  "observacao": "Aguardando manifestação do réu",
  "usuario": "cauã.salgado"
}
```

---

## ❌ Resposta de Erro Padronizada

```json
{
  "timestamp": "2026-05-26T17:12:06.158713",
  "status": 404,
  "erro": "Processo não encontrado com o id: 99"
}
```

---

# 7. ✨ Funcionalidades

---

## ⚙️ Back-end

- CRUD completo de processos judiciais
- Auditoria automática de alterações
- Log campo a campo das atualizações
- Validação com Bean Validation
- Tratamento centralizado de exceções
- Logs estruturados
- Constraint UNIQUE no banco
- Configuração de CORS
- Endpoints RESTful semânticos

---

## 💻 Front-end

- Listagem de processos
- Filtro por status
- Indicador visual de prazo
- Chips coloridos por status
- Cadastro com validação
- Atualização de status
- Histórico completo de alterações
- Persistência de usuário via localStorage
- Feedback visual de sucesso/erro

---

# 8. 🛡 Boas Práticas Aplicadas

---

## Conventional Commits

Todos os commits seguem:

```text
feat:
fix:
docs:
test:
chore:
```

---

## SOLID e Separação de Responsabilidades

| Camada | Objetivo |
|---|---|
| Controller | Entrada e saída HTTP |
| Service | Regras de negócio |
| Repository | Persistência |
| DTO | Contrato seguro da API |

---

## Segurança de Credenciais

- uso de variáveis de ambiente;
- nenhuma senha versionada;
- configuração segura para desenvolvimento.

---

## Logs Estruturados

Uso de:

- INFO
- WARN
- ERROR

com contexto suficiente para observabilidade em produção.

---

# 9. ⚖️ Decisões Técnicas e Trade-offs

---

## DTOs separados das entidades

### Motivo

- segurança;
- desacoplamento;
- controle do contrato da API.

### Trade-off

Mais classes e mapeamentos.

---

## FetchType.LAZY nos logs

### Motivo

Evitar carregamentos desnecessários.

### Trade-off

Necessidade de DTO específico para serialização.

---

## Auditoria campo a campo

### Motivo

Garantir rastreabilidade completa.

### Trade-off

Pequeno overhead em atualizações.

---

## ddl-auto: update em desenvolvimento

### Motivo

Agilidade durante avaliação técnica.

### Trade-off

Em produção o correto seria:

- Flyway;
- Liquibase;
- `ddl-auto: validate`.

---

## Monorepo

### Motivo

Facilidade de avaliação.

### Trade-off

Escalabilidade menor para equipes grandes.

---

# 10. 🚀 Melhorias Futuras

---

## 🔐 Segurança

- Spring Security + JWT
- RBAC por perfil
- Rate limiting

---

## 📊 Observabilidade

- Elasticsearch + Kibana
- Prometheus + Grafana
- Micrometer
- Jacoco

---

## 🏗 Arquitetura

- Docker
- Docker Compose
- Flyway
- Redis
- CI/CD com GitHub Actions
- Paginação
- Ordenação

---

## ⚖️ Negócio

- Integração com tribunais
- Workflow com Camunda BPM
- Notificações automáticas
- Kafka
- Upload de documentos
- Integração com LLMs

---

# 11. 🧪 Testes

---

## Executar testes

```bash
cd processojudicial

./mvnw test
```

---

## Cenários Cobertos

### ProcessoService

- criação com sucesso;
- criação duplicada;
- busca por ID;
- busca inexistente;
- listagem;
- remoção;
- validações de exceção.

---

## Resultado Atual

```text
7/7 testes passando
```

---

# 12. 👨‍💻 Autor

## Cauã Henrique Viana Salgado

Desenvolvedor Back-End  
Especialização em Arquitetura de Software Distribuído — PUC Minas

---

### 📬 Contato

- E-mail: `cauahenrique230503@gmail.com`
- LinkedIn: `linkedin.com/in/caua-henrique`
- GitHub: `github.com/CauaHvS`
- Localização: `Belo Horizonte/MG`

---

# ✅ Considerações Finais

O projeto foi desenvolvido priorizando:

- clareza arquitetural;
- confiabilidade;
- rastreabilidade;
- boas práticas modernas de engenharia;
- aderência ao domínio jurídico.

A proposta busca demonstrar não apenas capacidade de implementação, mas também maturidade técnica na construção de sistemas corporativos resilientes, auditáveis e preparados para evolução.