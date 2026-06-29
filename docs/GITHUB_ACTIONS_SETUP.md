# GitHub Actions & Deployment Setup Guide

## Overview

This guide covers:
1. **Phase 3**: GitHub Actions workflows (Build, Test, Docker Push)
2. **Phase 4-5**: Deployment to 02 Switch (SSH, Docker Compose)
3. **Secrets Management**: Secure configuration via GitHub Secrets

---

## Phase 3: GitHub Actions Workflows

### 1. Build & Test (`build.yml`)

**Triggers:**
- Push to `dev`, `main`, or `feat/**` branches
- Pull requests to `dev` or `main`

**What it does:**
- Builds backend with Maven (Java 21)
- Runs backend tests
- Builds frontend with npm (Node 20)
- Runs frontend tests
- Uploads build artifacts

**Status Check:**
- Blocks merge if build fails
- Runs in parallel for speed

---

### 2. Docker Build & Push (`docker-build.yml`)

**Triggers:**
- Push to `dev` or `main`
- Git tags `v*` (releases)
- Manual trigger via `workflow_dispatch`

**What it does:**
- Logs in to GitHub Container Registry (ghcr.io)
- Builds backend Docker image (multi-platform: linux/amd64, linux/arm64)
- Builds frontend Docker image
- Pushes images with smart tagging:
  - `latest` on `main` branch
  - `dev-latest` on `dev` branch
  - `v1.0.0` on release tags `v1.0.0`
  - Git SHA for all pushes
- Caches layers for faster builds

**Image Naming:**
```
ghcr.io/guillaumeeven/ecotrack-office/ecotrack-backend:latest
ghcr.io/guillaumeeven/ecotrack-office/ecotrack-frontend:latest
```

---

### 3. Integration Tests (`integration-tests.yml`)

**Triggers:**
- Pull requests to `dev` or `main`
- Push to `dev`
- Manual trigger

**What it does:**
- Builds full Docker Compose stack
- Starts all 3 services (MySQL, Backend, Frontend)
- Waits for health checks (30s timeout)
- Tests API endpoints
- Uploads logs on failure
- Cleans up containers

**Benefits:**
- Full-stack testing before merge
- Catches integration issues early
- MySQL + API + Frontend working together

---

## Phase 4-5: Deployment to 02 Switch

### Setup Requirements

#### 1. 02 Switch Server Preparation

```bash
# SSH into 02 Switch
ssh user@02-switch-ip

# Install Docker (if not already done)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version

# Create deployment directory
mkdir -p ~/ecotrack-office
cd ~/ecotrack-office
```

#### 2. GitHub Secrets Configuration

Navigate to: **Settings → Secrets and variables → Actions**

**Required secrets:**

| Secret | Description | Example |
|--------|-------------|---------|
| `DEPLOY_KEY` | SSH private key for 02 Switch | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `DEPLOY_HOST_PROD` | Production server IP/hostname | `192.168.1.100` or `prod.example.com` |
| `DEPLOY_USER_PROD` | SSH user for production | `deploy` |
| `DEPLOY_HOST_STAGING` | Staging server IP/hostname | `192.168.1.101` or `staging.example.com` |
| `DEPLOY_USER_STAGING` | SSH user for staging | `deploy` |
| `DB_PASSWORD` | MySQL database password | `SecurePassword123!` |
| `DB_USERNAME` | MySQL database user | `ecotrack_user` |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `RootPassword123!` |
| `JWT_SECRET` | JWT signing secret (32+ chars) | See generation instructions below |
| `SLACK_WEBHOOK` | Slack notification webhook (optional) | `https://hooks.slack.com/...` |

**Generate JWT_SECRET:**

```bash
openssl rand -base64 32
# Output example: gK9vL2mNpQ8xW5yRjH3zBcDeFgIjKlMnOpQrStUvWxYz==
```

**Create SSH Key for Deployment:**

```bash
# Generate SSH key (no passphrase!)
ssh-keygen -t ed25519 -f deploy_key -N ""

# Add public key to 02 Switch
ssh-copy-id -i deploy_key.pub user@02-switch-ip
# or manually add deploy_key.pub content to ~/.ssh/authorized_keys

# Copy private key to GitHub Secret
cat deploy_key  # Copy the entire content to DEPLOY_KEY secret

# Clean up local copy
rm deploy_key*
```

#### 3. Environment Variables on Server

Create `.env.prod` on 02 Switch:

```bash
ssh user@02-switch-ip
cd ~/ecotrack-office

cat > .env.prod << 'EOF'
DB_NAME=ecotrack
DB_USERNAME=ecotrack_user
DB_PASSWORD=${DB_PASSWORD}
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}

SPRING_PROFILE=prod
SERVER_PORT=8080

JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=86400000

CORS_ALLOWED_ORIGINS=https://ecotrack.example.com
ANGULAR_API_URL=https://ecotrack.example.com/api
API_BASE_URL=http://backend:8080/api

DOCKER_REGISTRY=ghcr.io/guillaumeeven/
VERSION=latest

LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_EDIAE=INFO
EOF

chmod 600 .env.prod
```

---

### Deploy Workflow (`deploy.yml`)

**Triggers:**
- Push to `main` → Deploy to **production** (02 Switch prod)
- Push to `dev` → Deploy to **staging** (02 Switch staging)
- Manual trigger with environment selection
- Git tags `deploy-*` → Custom deployment

**Workflow Steps:**

1. **Setup Environment** - Determines target (prod/staging)
2. **Pre-Deployment Checks** - Validates secrets & compose files
3. **Deploy** - SSH to server, pulls images, starts containers
4. **Health Checks** - Verifies all services are healthy
5. **Rollback** (on failure) - Restarts previous version
6. **Notification** - Sends Slack message with status

**Deployment Scripts on Server:**

- `deploy.sh` - Main deployment orchestrator
  - Creates backup
  - Pulls Docker images
  - Gracefully stops old services
  - Starts new services
  - Performs health checks

- `backup.sh` - Backup manager
  - MySQL database dump
  - Volume backups
  - Old backup cleanup (7-day retention)

---

## Deployment Flow

```
Developer Push to main
        ↓
GitHub Actions triggered
        ↓
Build & Test (build.yml)
        ├─ Backend build
        ├─ Frontend build
        └─ Integration tests
        ↓
Docker Build & Push (docker-build.yml)
        ├─ Build backend image
        ├─ Push to ghcr.io
        ├─ Build frontend image
        └─ Push to ghcr.io
        ↓
Deploy to 02 Switch (deploy.yml)
        ├─ SSH connection
        ├─ Create backup
        ├─ Pull images
        ├─ Stop old services
        ├─ Start new services
        ├─ Health checks
        └─ Notification
```

---

## Manual Testing

### Test locally first:

```bash
# Copy dev env
cp .env.dev .env

# Start stack
docker-compose up -d

# Check services
docker-compose ps

# Test API
curl http://localhost:8080/actuator/health

# Test frontend
curl http://localhost/health
```

### SSH to 02 Switch and check deployment:

```bash
# SSH to server
ssh user@02-switch-ip
cd ~/ecotrack-office

# View logs
docker-compose -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.prod.yml logs -f frontend

# Check health
docker-compose -f docker-compose.prod.yml exec backend curl http://localhost:8080/actuator/health

# Restart if needed
docker-compose -f docker-compose.prod.yml restart

# Rollback to previous version (if something went wrong)
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
```

---

## Troubleshooting

### Deploy fails: "Connection refused"
- Check SSH key is correct in GitHub Secret
- Verify server is reachable: `ping 02-switch-ip`
- Ensure Docker daemon is running on server

### Health check timeout
- SSH to server and check logs: `docker-compose logs backend`
- Verify environment variables are set correctly
- Check database connectivity: `docker-compose exec backend curl http://localhost:8080/actuator/health`

### Images not found in registry
- Check GitHub Secrets GITHUB_TOKEN is correct
- Verify build.yml completed successfully
- Check registry: `docker pull ghcr.io/guillaumeeven/ecotrack-office/ecotrack-backend:latest`

### Database connection error
- Check DB_PASSWORD matches between .env.prod and GitHub Secret
- Verify MySQL container started: `docker-compose ps mysql`
- Check MySQL logs: `docker-compose logs mysql`

---

## Security Best Practices

1. **Rotate Secrets Regularly**
   - Change JWT_SECRET every 6 months
   - Rotate DB_PASSWORD every 3 months
   - Refresh SSH keys if compromised

2. **Backup Strategy**
   - Backups run before every deployment
   - Keep 7-day retention (configurable)
   - Test restore procedures monthly

3. **Access Control**
   - Limit GitHub Secret access to admins only
   - Use separate SSH keys per environment
   - Monitor GitHub Actions logs for suspicious activity

4. **Monitoring & Alerts**
   - Set up Slack notifications (configure SLACK_WEBHOOK)
   - Monitor deployment logs
   - Health checks verify service availability

---

## Next Steps

1. ✅ **Phase 3 Complete**: Workflows are live
2. **Phase 4-5 Next**: Finalize 02 Switch setup and test deployment
3. Test deploying to staging first before production
4. Set up monitoring/alerting
5. Document runbook for team

---

## Quick Reference

**Deploy to staging (dev):**
```bash
git commit -am "feature: something"
git push origin dev
# GitHub Actions automatically deploys to staging
```

**Deploy to production (main):**
```bash
git checkout main
git merge --no-ff dev
git push origin main
# GitHub Actions automatically deploys to production
```

**Manual deployment trigger:**
- Go to **Actions → Deploy to 02 Switch → Run workflow**
- Select environment: staging or production
- Click "Run workflow"

**View deployment logs:**
- Go to **Actions → Deploy to 02 Switch → Latest run**
- Click on the job to see detailed logs

---

For questions or issues, check the deployment logs in GitHub Actions or SSH to 02 Switch for server logs.
