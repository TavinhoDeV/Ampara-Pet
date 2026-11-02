# AMPARA

### Sistema de Triagem e Acolhimento de Animais Urbanos

Plataforma para centralizar e acompanhar o processo de resgate, triagem, tratamento e adoção de animais em situação de rua. Projeto acadêmico de Análise e Desenvolvimento de Sistemas, conectado aos conceitos de Cidades Inteligentes e Saúde Única.

## Arquitetura

O projeto foi migrado de Jakarta Faces/PrimeFaces + WildFly para uma arquitetura em duas partes:

| Parte | Tecnologias |
|---|---|
| `backend/` | Java 25, Spring Boot 3.5, Spring Data JPA (Hibernate), Spring Security + JWT, Flyway, PostgreSQL, Maven |
| `frontend/` | React 18 + Vite (SPA que consome a API REST) |
| Infra local | Docker Compose (PostgreSQL 17) |

```
ampara/
├── docker-compose.yml
├── backend/
│   └── src/main/
│       ├── java/br/com/ampara/
│       │   ├── config/       # segurança, JWT, CORS, seed do admin
│       │   ├── controller/   # endpoints REST
│       │   ├── dto/          # contratos de entrada e saída
│       │   ├── exception/    # exceções e tratamento global de erros
│       │   ├── model/        # entidades JPA e enums
│       │   ├── repository/   # Spring Data JPA (substitui os DAOs)
│       │   └── service/      # regras de negócio
│       └── resources/
│           ├── application.yml
│           └── db/migration/ # Flyway
└── frontend/
    └── src/ (App, api.js, pages/Login, pages/Animais, styles.css)
```

Os controllers só recebem e devolvem dados; **as regras de negócio ficam nos services**. Os DAOs planejados foram substituídos por repositórios Spring Data JPA, que usam o Hibernate como provedor.

## Como executar

Pré-requisitos: JDK 25 (ou 21, ajustando `java.version` no `pom.xml`), Maven, Node 18+ e Docker.

```bash
# 1. Banco de dados
docker compose up -d

# 2. API (http://localhost:8080)
cd backend
mvn spring-boot:run

# 3. Front-end (http://localhost:5173), em outro terminal
cd frontend
npm install
npm run dev
```

No primeiro start, se o banco não tiver usuários, a API cria um administrador:

- e-mail: `admin@ampara.local`
- senha: `Admin#12345`

Troque esses valores com as variáveis `ADMIN_EMAIL` e `ADMIN_SENHA` (e defina `JWT_SECRET`, com no mínimo 32 caracteres, fora do ambiente de desenvolvimento).

Testes: `cd backend && mvn test`

## Perfis e permissões

| Perfil | Pode |
|---|---|
| **VOLUNTARIO** (cadastro público) | registrar resgates, consultar animais, registrar atendimentos (sem validar etapas) |
| **CLINICA** | tudo do voluntário + validar vacinação/castração + editar animais |
| **ADMIN** | tudo + registrar adoções, excluir animais, gerenciar usuários |

## Endpoints

| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/auth/login` | público |
| POST | `/api/auth/cadastro` | público (cria VOLUNTARIO) |
| GET | `/api/usuarios/me` | autenticado |
| GET, POST | `/api/usuarios` | ADMIN |
| GET | `/api/animais?status=` | autenticado |
| GET | `/api/animais/{id}` | autenticado |
| PUT | `/api/animais/{id}` | CLINICA, ADMIN |
| DELETE | `/api/animais/{id}` | ADMIN |
| GET, POST | `/api/animais/{id}/atendimentos` | autenticado (validar: CLINICA, ADMIN) |
| GET, POST | `/api/resgates` | autenticado |
| GET, POST | `/api/adocoes` | ADMIN |

As rotas protegidas exigem o cabeçalho `Authorization: Bearer <token>`, devolvido pelo login.

## Regra de negócio central

A adoção só é registrada quando **vacinação e castração foram validadas** por clínica ou administração. A verificação está em `AdocaoService`, não na interface, e é coberta por teste unitário. Se faltar algo, a API responde `422` dizendo o que está pendente.

Ciclo do animal: `RESGATADO → EM_TRIAGEM → EM_TRATAMENTO → APTO_ADOCAO → ADOTADO`.

## Pendências

- Recuperação de senha
- Gestão de instituições/clínicas parceiras (a tabela e a entidade já existem)
- Dashboard administrativo
- Testes de integração com Testcontainers
- Trocar `ddl-auto: none` por `validate` depois do primeiro start bem-sucedido
