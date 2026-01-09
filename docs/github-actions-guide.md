# Guia: CI/CD Seguro com GitHub Actions para Chrono Letter

Este guia cobre a configuração completa do pipeline CI/CD com **todas as práticas de segurança** aplicadas.

---

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Pré-requisitos](#2-pré-requisitos)
3. [Configurar SSH Deploy Key](#3-configurar-ssh-deploy-key)
4. [Configurar Environment Production](#4-configurar-environment-production)
5. [Configurar Self-hosted Runner](#5-configurar-self-hosted-runner)
6. [Primeiro Deploy](#6-primeiro-deploy)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. Visão Geral

### Arquitetura Segura

```
┌─────────────────┐     ┌──────────────────────────────────────────────────┐
│  Seu PC         │     │  GitHub                                          │
│  (Windows)      │     │                                                  │
├─────────────────┤     ├──────────────────────────────────────────────────┤
│                 │     │                                                  │
│ git push ───────┼────►│ Job: build      (ubuntu-24.04)                   │
│                 │     │ Job: docker     (ubuntu-24.04) → GHCR            │
│                 │     │ Job: deploy ────┼──► Self-hosted Runner          │
│                 │     │ Job: sync-pub ──┼──► chrono-letter-pub (SSH)     │
└─────────────────┘     └──────────────────────────────────────────────────┘
                                                    │
                                                    ▼
                        ┌──────────────────────────────────────────────────┐
                        │  Seu Servidor (Debian)                           │
                        ├──────────────────────────────────────────────────┤
                        │  Self-hosted Runner ───► docker compose up       │
                        │  (usa GITHUB_TOKEN, não PAT)                     │
                        └──────────────────────────────────────────────────┘
```

### Repositórios

| Repo | Visibilidade | Secrets | Runner |
|------|--------------|---------|--------|
| `chrono-letter` | 🔒 Privado | `PUBLIC_REPO_SSH_KEY` | ✅ Self-hosted |
| `chrono-letter-pub` | 🌍 Público | Nenhum | ❌ Nenhum |

### Medidas de Segurança Aplicadas

| Medida | Descrição |
|--------|-----------|
| ✅ Permissions mínimas | `contents: read` global, elevação por job |
| ✅ Actions pinadas por SHA | Proteção contra supply chain attacks |
| ✅ SSH Deploy Key | Substitui PAT (acesso limitado ao repo público) |
| ✅ docker/login-action | Gerenciamento seguro de credenciais |
| ✅ ubuntu-24.04 | Versão fixa (reprodutibilidade) |
| ✅ Environment production | Gate de aprovação para deploy |
| ✅ Logout automático | Limpa credenciais após uso |
| ✅ Sem tag latest | Deploy por SHA específico |

---

## 2. Pré-requisitos

- [ ] Repositório `chrono-letter` privado criado
- [ ] Repositório `chrono-letter-pub` público criado
- [ ] Docker instalado no servidor
- [ ] Acesso SSH ao servidor

---

## 3. Configurar SSH Deploy Key

A SSH Deploy Key permite que o repo privado faça push para o público **sem usar PAT**.

### 3.1 Gerar o par de chaves

No seu PC (PowerShell ou terminal):

```powershell
# Gerar chave Ed25519 (mais segura)
ssh-keygen -t ed25519 -C "chrono-letter-sync" -f chrono-sync-key -N ""
```

Isso cria dois arquivos:
- `chrono-sync-key` (chave **privada**)
- `chrono-sync-key.pub` (chave **pública**)

### 3.2 Adicionar chave PÚBLICA no repo público

1. Vá em: `chrono-letter-pub` → **Settings** → **Deploy keys**
2. Clique em **Add deploy key**
3. Configure:
   - Title: `chrono-letter-sync`
   - Key: Cole o conteúdo de `chrono-sync-key.pub`
   - ✅ **Allow write access** (marcar!)
4. Clique em **Add key**

### 3.3 Adicionar chave PRIVADA no repo privado

1. Vá em: `chrono-letter` → **Settings** → **Secrets and variables** → **Actions**
2. Clique em **New repository secret**
3. Configure:
   - Name: `PUBLIC_REPO_SSH_KEY`
   - Secret: Cole o conteúdo de `chrono-sync-key` (a privada!)
4. Clique em **Add secret**

### 3.4 Limpar arquivos locais

```powershell
# Apagar as chaves do seu PC (já estão salvas no GitHub)
Remove-Item chrono-sync-key, chrono-sync-key.pub
```

---

## 4. Configurar Environment Production

O Environment adiciona uma camada de aprovação antes do deploy.

### 4.1 Criar o Environment

1. Vá em: `chrono-letter` → **Settings** → **Environments**
2. Clique em **New environment**
3. Nome: `production`
4. Clique em **Configure environment**

### 4.2 Configurar proteções (opcional)

| Opção | Recomendação |
|-------|--------------|
| Required reviewers | Adicione você mesmo (para aprovar deploys) |
| Wait timer | 0 minutos |
| Deployment branches | `main` only |

> ⚠️ Se não quiser aprovação manual, deixe sem reviewers. O deploy será automático após os testes passarem.

---

## 5. Configurar Self-hosted Runner

### 5.1 Criar pasta e compose no servidor

```bash
# No servidor
mkdir -p ~/runner
cd ~/runner

# Criar compose.yaml
cat > compose.yaml << 'EOF'
name: github-runner

services:
  runner:
    image: myoung34/github-runner:2.321.0  # Pinar versão, não usar :latest
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
    user: root
    security_opt:
      - label:disable

volumes:
  runner_work:
EOF
```

> ⚠️ Substitua `SEU_USER` pelo seu username do GitHub.

### 5.2 Gerar token de registro

1. Vá em: `chrono-letter` → **Settings** → **Actions** → **Runners**
2. Clique em **New self-hosted runner**
3. Escolha: **Linux**, **x64**
4. Copie o **token** (começa com `A...`)

> ⚠️ O token expira em 1 hora!

### 5.3 Subir o runner

```bash
cd ~/runner

# Criar .env com o token
echo "GITHUB_RUNNER_TOKEN=SEU_TOKEN_AQUI" > .env

# Subir
docker compose up -d

# Verificar
docker logs github-runner-chrono
```

### 5.4 Verificar no GitHub

Vá em: **Settings** → **Actions** → **Runners**

Deve aparecer: **chrono-server** com status **Idle** ✅

---

## 6. Primeiro Deploy

### 6.1 Preparar compose.prod.yaml no servidor

```bash
mkdir -p /srv/DATA/chrono-letter
cd /srv/DATA/chrono-letter

# Criar ou copiar o compose.prod.yaml
nano compose.prod.yaml
```

> ⚠️ O compose.prod.yaml precisa usar a variável `IMAGE_TAG`:
> ```yaml
> image: ghcr.io/rpsouza441/chrono-letter:${IMAGE_TAG:-latest}
> ```

### 6.2 Fazer push

```powershell
git add .
git commit -m "feat: secure CI/CD pipeline"
git push origin main
```

### 6.3 Acompanhar

Vá em: **Actions** → Veja o pipeline rodando

Resultado esperado:
- ✅ build: passou
- ✅ docker: imagem publicada
- ⏳ deploy: aguardando aprovação (se configurou reviewers)
- ✅ sync-public: código sincronizado

---

## 7. Troubleshooting

### Sync falha com "Permission denied (publickey)"

```bash
# Verificar se a deploy key foi adicionada corretamente
# A chave PÚBLICA deve estar no chrono-letter-pub
# A chave PRIVADA deve estar no secret PUBLIC_REPO_SSH_KEY
```

### Deploy falha com "unauthorized"

O deploy agora usa `GITHUB_TOKEN` (não PAT). Verifique:
1. O package GHCR está vinculado ao repositório?
2. O workflow tem `permissions: packages: read`?

### Runner não aparece

```bash
docker logs github-runner-chrono

# Se token expirado, gere um novo e:
docker compose down
echo "GITHUB_RUNNER_TOKEN=NOVO_TOKEN" > .env
docker compose up -d
```

### --force-with-lease falha

Isso acontece se o histórico do repo público divergiu. Solução:

```bash
# No chrono-letter-pub, faça reset:
git fetch origin
git reset --hard origin/main
```

---

## Checklist Final

### Secrets Configurados

- [ ] `PUBLIC_REPO_SSH_KEY` (chave privada SSH)

### GitHub Settings

- [ ] Deploy Key no `chrono-letter-pub` (chave pública, write access)
- [ ] Environment `production` criado
- [ ] Runner self-hosted aparece como "Idle"

### Servidor

- [ ] Runner rodando (`docker ps | grep runner`)
- [ ] compose.prod.yaml em `/srv/DATA/chrono-letter/`
- [ ] compose.prod.yaml usa `${IMAGE_TAG}`

### Teste

- [ ] Push para main dispara pipeline
- [ ] Todos os jobs passam
- [ ] App rodando no servidor
