# Guia: CI/CD com GitHub Actions para Chrono Letter

Este guia foi **testado e validado** passo a passo.

---

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Arquivos Necessários](#2-arquivos-necessários)
3. [Configurar Secrets no GitHub](#3-configurar-secrets-no-github)
4. [Configurar Self-hosted Runner](#4-configurar-self-hosted-runner)
5. [Primeiro Deploy](#5-primeiro-deploy)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. Visão Geral

### Arquitetura

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────────────────┐
│  Seu PC         │     │  GitHub         │     │  Seu Servidor (Debian)      │
│  (Windows)      │     │                 │     │                             │
├─────────────────┤     ├─────────────────┤     ├─────────────────────────────┤
│                 │     │                 │     │  ┌─────────────────────┐    │
│ git push ───────┼────►│ Job: build      │     │  │ Self-hosted Runner  │    │
│                 │     │ Job: docker     │     │  │ (container Docker)  │    │
│                 │     │       │         │     │  │         │           │    │
│                 │     │       ▼         │◄────┼──│ "Tem job pra mim?"  │    │
│                 │     │ Job: deploy ────┼────►│  │         ▼           │    │
│                 │     │                 │     │  │ Executa deploy      │    │
│                 │     │ Job: sync-pub   │     │  └─────────────────────┘    │
└─────────────────┘     └─────────────────┘     └─────────────────────────────┘
```

### Repositórios

| Repo | Visibilidade | Função |
|------|--------------|--------|
| `chrono-letter` | Privado | Desenvolvimento + CI/CD |
| `chrono-letter-pub` | Público | Portfólio (sync automático) |

### Jobs do Pipeline

| Job | Onde roda | O que faz |
|-----|-----------|-----------|
| `build` | GitHub | Compila e testa (Maven) |
| `docker` | GitHub | Builda imagem e publica no GHCR |
| `deploy` | **Seu servidor** | Faz pull e restart dos containers |
| `sync-public` | GitHub | Copia código para repo público |

---

## 2. Arquivos Necessários

### Estrutura do projeto

```
chrono-letter/
├── .github/
│   └── workflows/
│       └── ci-cd.yml        # Pipeline CI/CD
├── docker/
│   ├── Dockerfile           # Build da aplicação
│   ├── compose.yaml         # Dev (PostgreSQL + MailHog)
│   ├── compose.prod.yaml    # Prod (App + PostgreSQL + MailHog)
│   └── compose.runner.yaml  # Self-hosted Runner
├── .dockerignore
├── src/
└── pom.xml
```

### Arquivos no servidor

```
/home/SEU_USER/
├── runner/
│   ├── compose.yaml         # compose.runner.yaml
│   └── .env                 # GITHUB_RUNNER_TOKEN
└── apps/
    └── chrono-letter/
        └── compose.prod.yaml
```

---

## 3. Configurar Secrets no GitHub

Vá em: **chrono-letter (privado) → Settings → Secrets and variables → Actions**

### Secret necessário:

| Nome | Valor | Descrição |
|------|-------|-----------|
| `PAT_TOKEN` | Token de acesso pessoal | Para sync com repo público |

### Como criar o PAT_TOKEN:

1. **GitHub → Settings (seu perfil) → Developer settings**
2. **Personal access tokens → Tokens (classic)**
3. **Generate new token (classic)**
4. Configure:
   - Nome: `chrono-letter-sync`
   - Expiração: 90 dias
   - Scopes: ✅ `repo` (marcar apenas este)
5. **Generate token** → Copie
6. Adicione como secret `PAT_TOKEN` no repo privado

---

## 4. Configurar Self-hosted Runner

### 4.1 Por que Self-hosted?

- Seu servidor está em rede local (sem IP público)
- Não precisa abrir portas no firewall
- O runner **puxa** jobs do GitHub (conexão de saída)

### 4.2 Copiar compose.runner.yaml para o servidor

```bash
# No servidor, criar pasta do runner
mkdir -p ~/runner
cd ~/runner

# Criar o compose.yaml (cole o conteúdo de docker/compose.runner.yaml)
nano compose.yaml
```

Conteúdo do arquivo:

```yaml
name: github-runner

services:
  runner:
    image: myoung34/github-runner:latest
    container_name: github-runner-chrono
    restart: unless-stopped
    environment:
      RUNNER_NAME: chrono-server
      RUNNER_WORKDIR: /tmp/runner/work
      RUNNER_SCOPE: repo
      REPO_URL: https://github.com/SEU_USER/chrono-letter
      ACCESS_TOKEN: ${GITHUB_RUNNER_TOKEN}
      LABELS: self-hosted,linux,x64,docker
      RUNNER_EPHEMERAL: "false"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - runner_work:/tmp/runner/work
      - ~/apps:/home/runner/apps  # Ajuste conforme seu usuário
    user: root
    security_opt:
      - label:disable

volumes:
  runner_work:
```

> ⚠️ Ajuste `REPO_URL` e o volume `~/apps` conforme seu setup.

### 4.3 Gerar token de registro

1. Vá em: **chrono-letter (privado) → Settings → Actions → Runners**
2. Clique em **New self-hosted runner**
3. Escolha: **Linux**, **x64**
4. Na seção "Configure", copie o **token** (começa com `A...`)

> ⚠️ O token expira em 1 hora! Use logo após gerar.

### 4.4 Criar .env e subir o runner

```bash
cd ~/runner

# Criar .env com o token
echo "GITHUB_RUNNER_TOKEN=SEU_TOKEN_AQUI" > .env

# Subir o runner
docker compose up -d

# Verificar logs
docker logs github-runner-chrono
```

### 4.5 Verificar no GitHub

Vá em: **Settings → Actions → Runners**

Deve aparecer:
- **chrono-server** com status **Idle** (bolinha verde)

---

## 5. Primeiro Deploy

### 5.1 Copiar compose.prod.yaml para o servidor

```bash
mkdir -p ~/apps/chrono-letter
cd ~/apps/chrono-letter
nano compose.prod.yaml  # Cole o conteúdo de docker/compose.prod.yaml
```

### 5.2 Fazer commit e push

```powershell
# No Windows
git add .
git commit -m "feat: setup CI/CD pipeline"
git push origin main
```

### 5.3 Acompanhar no GitHub

Vá em: **Actions** → Veja o pipeline rodando

Resultado esperado:
- ✅ build: passou
- ✅ docker: imagem publicada
- ✅ deploy: containers atualizados
- ✅ sync-public: código copiado para repo público

---

## 6. Troubleshooting

### Runner não aparece no GitHub

```bash
# Ver logs do container
docker logs github-runner-chrono

# Erros comuns:
# - Token expirado: gere um novo
# - Rede: verifique se consegue acessar github.com
```

### Deploy falha com "permission denied"

```bash
# O runner precisa de acesso ao docker.sock
# Verificar se o volume está correto:
docker exec github-runner-chrono ls -la /var/run/docker.sock
```

### Sync falha com "authentication failed"

- Verifique se o `PAT_TOKEN` está correto
- Verifique se o token tem scope `repo`
- Verifique se o repo público existe

### Imagem não é encontrada no GHCR

- Primeira vez demora alguns minutos
- Verifique se o repo está **privado** (pacotes privados por padrão)
- Vá em: **Packages** no GitHub e torne público se necessário

---

## Checklist Final

- [ ] PAT_TOKEN criado e adicionado como secret
- [ ] compose.runner.yaml no servidor
- [ ] Token de registro do runner gerado
- [ ] Runner rodando (`docker compose up -d`)
- [ ] Runner aparece como "Idle" no GitHub
- [ ] compose.prod.yaml no servidor
- [ ] Primeiro push feito
- [ ] Pipeline passou
- [ ] App rodando (`docker ps`)
