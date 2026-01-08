# Chrono Letter

> Envie cartas para você mesmo no futuro

Sistema de agendamento de emails que permite escrever mensagens para serem entregues em uma data futura. Inspirado no [FutureMe](https://www.futureme.org/).

---

## Features

- Escreva cartas para você ou outros
- Agende a entrega para qualquer data futura
- Criptografia E2E - só você lê suas mensagens
- Login fácil - OAuth Google ou Magic Link
- Entrega garantida - retry automático por 48h
- GDPR compliant - exporte ou delete seus dados

---

## Tech Stack

| Camada | Tecnologia |
|--------|------------|
| Backend | Java 21, Spring Boot 4 |
| Database | PostgreSQL 16 |
| Auth | OAuth2 (Google), Magic Link |
| Email | SMTP / AWS SES |
| Infra | Docker, GitHub Actions |

---

## Quick Start

### Pré-requisitos

- Java 21+
- Docker & Docker Compose
- Maven (ou use o wrapper `./mvnw`)

### 1. Clone e configure

```bash
git clone https://github.com/rpsouza441/chrono-letter.git
cd chrono-letter

# Copiar arquivos de configuração
cp .env.example .env
cp src/main/resources/dev-master-key.txt.example src/main/resources/dev-master-key.txt
cp src/test/resources/test-master-key.txt.example src/test/resources/test-master-key.txt
```

### 2. Suba o banco de dados

```bash
docker compose -f docker/compose.yaml up -d
```

Serviços disponíveis:
- PostgreSQL: `localhost:5432`
- MailHog UI: http://localhost:8025

### 3. Rode a aplicação

```bash
./mvnw spring-boot:run
```

API disponível em: http://localhost:8080

---

## Configuração de Chaves

O projeto usa master keys para criptografia. **Nunca commite chaves reais!**

| Arquivo | Descrição |
|---------|-----------|
| `*-master-key.txt.example` | Template (vai pro Git) |
| `*-master-key.txt` | Chave real (no .gitignore) |

### Gerar sua própria chave (opcional):

```bash
openssl rand -base64 32 > src/main/resources/dev-master-key.txt
```

---

## Estrutura do Projeto

```
chrono-letter/
├── src/
│   ├── main/
│   │   ├── java/          # Código fonte
│   │   └── resources/     # Configs + master key
│   └── test/              # Testes
├── docker/
│   ├── Dockerfile         # Build da aplicação
│   ├── compose.yaml       # Dev (PostgreSQL + MailHog)
│   └── compose.prod.yaml  # Produção
├── .github/
│   └── workflows/         # CI/CD
└── docs/                  # Documentação
```

---

## Testes

```bash
# Rodar todos os testes
./mvnw test

# Rodar com coverage
./mvnw verify
```

---

## Deploy

O projeto usa GitHub Actions para CI/CD automático:

1. Push na main - Dispara pipeline
2. Build & Test - Compila e testa
3. Docker - Cria imagem e publica no GHCR
4. Deploy - Atualiza containers no servidor

Veja [docs/github-actions-guide.md](docs/github-actions-guide.md) para detalhes.

---

## Licença

MIT

---

## Contribuindo

1. Fork o projeto
2. Crie sua branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'feat: nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request