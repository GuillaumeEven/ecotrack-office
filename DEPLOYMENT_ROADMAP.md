# 🚀 EcoTrack Office - Deployment Roadmap

**Objectif:** Containeriser l'application complète (Angular + Spring Boot + MySQL) et mettre en place un pipeline CI/CD automatisé avec déploiement sur 02 Switch.

**Timeline estimée:** 5 phases sur ~2-3 sprints

---

## 📋 Vue d'ensemble des phases

```
Phase 1: Dockerisation         → Front + Back + DB
   ↓
Phase 2: Docker Compose        → Dev + Prod
   ↓
Phase 3: GitHub Actions        → Build & Push to Registry
   ↓
Phase 4: Deploy Config         → 02 Switch Setup
   ↓
Phase 5: Pipeline Complet      → CI/CD automatisé end-to-end
```

---

## Phase 1: Dockerisation de base (Frontend + Backend + Database)

### 1.1 Backend Dockerfile (Spring Boot)
**Fichier:** `back/ecotrack-office/Dockerfile`

**Objectives:**
- [ ] Créer un Dockerfile multi-stage pour Spring Boot
- [ ] Build JAR en stage 1
- [ ] Runtime image légère (Eclipse Temurin) en stage 2
- [ ] Port: `8080`
- [ ] Health check inclus

**Dépendances:**
- pom.xml existant
- Maven disponible

**Notes:**
- Utiliser Java 21+ (compatible avec Spring Boot 3.x)
- Optimiser les layers pour cache Docker

---

### 1.2 Frontend Dockerfile (Angular)
**Fichier:** `front/ecotrack-office/Dockerfile`

**Objectives:**
- [ ] Créer un Dockerfile multi-stage pour Angular
- [ ] Build Node en stage 1 (npm build)
- [ ] Serve via Nginx en stage 2
- [ ] Port: `80`
- [ ] Health check inclus

**Dépendances:**
- package.json existant
- Angular CLI

**Notes:**
- Production build avec AOT
- Nginx config personnalisée (routing Angular)

---

### 1.3 MySQL Configuration
**Fichier:** `back/ecotrack-office/schema.sql` (existant)

**Objectives:**
- [ ] Valider schema.sql pour Docker init
- [ ] Créer init-db.sh si besoin (seed data, users, etc.)
- [ ] Variables d'env pour credentials

**Dépendances:**
- Schema existant fonctionnel

---

## Phase 2: Docker Compose (Development + Production)

### 2.1 docker-compose.yml (Development)
**Fichier:** `docker-compose.yml` (racine)

**Objectives:**
- [ ] 3 services: backend, frontend, mysql
- [ ] Volumes pour hot-reload (dev)
- [ ] Networks pour inter-service communication
- [ ] Environment variables séparées (.env.dev)
- [ ] Ports exposés: 80 (front), 8080 (back), 3306 (db)

**Features:**
- [ ] Backend sur port 8080
- [ ] Frontend sur port 80 → proxie vers backend
- [ ] MySQL avec persistence
- [ ] Logs centralisés

---

### 2.2 docker-compose.prod.yml (Production)
**Fichier:** `docker-compose.prod.yml`

**Objectives:**
- [ ] Images optimisées (pas de volumes dev)
- [ ] Restart policies: always
- [ ] Resource limits (memory, CPU)
- [ ] Logging driver pour 02 Switch
- [ ] Variables d'env sécurisées (.env.prod)

**Features:**
- [ ] No volumes (images finales)
- [ ] Health checks actifs
- [ ] Load balancer ready (ports désignés)
- [ ] Secrets gérés (DB password, JWT keys, etc.)

---

### 2.3 Configuration d'environnement
**Fichiers:** `.env.dev`, `.env.prod`, `.env.example`

**Variables requises:**
```
# Database
DB_HOST=mysql
DB_PORT=3306
DB_NAME=ecotrack_db
DB_USER=ecotrack_user
DB_PASSWORD=***

# Backend
SPRING_PROFILE=dev|prod
JWT_SECRET=***
API_URL=http://localhost:8080

# Frontend
ANGULAR_API_URL=http://localhost:8080/api

# Deployment
DOCKER_REGISTRY=***
DOCKER_REPO=ecotrack-office
```

---

## Phase 3: GitHub Actions (Build, Test, Push)

### 3.1 Workflow: Build & Test
**Fichier:** `.github/workflows/build.yml`

**Trigger:** `push` sur `dev`, `main`, `feat/**`

**Jobs:**
- [ ] Checkout code
- [ ] Setup Java 21 + Maven
- [ ] Run backend tests
- [ ] Setup Node + npm
- [ ] Run frontend tests
- [ ] Build artifacts (JAR + dist/)

**Success criteria:**
- Tous les tests passent
- Build artifacts créés

---

### 3.2 Workflow: Docker Build & Push
**Fichier:** `.github/workflows/docker-build.yml`

**Trigger:** `push` sur `dev`, `main`; `release` tags

**Jobs:**
- [ ] Setup Docker Buildx
- [ ] Login to Docker Registry (ghcr.io ou autre)
- [ ] Build backend image
- [ ] Build frontend image
- [ ] Push images avec tags:
  - `latest` (sur `main`)
  - `dev-latest` (sur `dev`)
  - `v1.0.0` (sur release tags)

**Features:**
- [ ] Multi-platform builds (linux/amd64, linux/arm64)
- [ ] Cache layers
- [ ] SBOM generation (dépendances)

---

### 3.3 Workflow: Integration Test
**Fichier:** `.github/workflows/integration-tests.yml`

**Trigger:** `pull_request`, `push` sur `dev`

**Jobs:**
- [ ] Docker Compose up
- [ ] Wait for health checks
- [ ] Run E2E tests (Cypress/Playwright)
- [ ] Cleanup

**Features:**
- [ ] Full stack test en Docker
- [ ] Screenshots/logs on failure
- [ ] Artifact upload

---

## Phase 4: Configuration Déploiement 02 Switch

### 4.1 02 Switch Setup
**Fichier:** `deployment/02-switch-config.yml`

**Objectives:**
- [ ] Documentation accès 02 Switch
- [ ] SSH keys setup
- [ ] Docker daemon configuré
- [ ] Docker Registry credentials
- [ ] Systemd service pour docker-compose
- [ ] Monitoring setup (logs, healthchecks)

**Prérequis:**
- [ ] Accès SSH au serveur 02 Switch
- [ ] Docker & Docker Compose installés
- [ ] Registre Docker accessible (privé ou public)

---

### 4.2 Deployment Secrets
**Fichier:** `.github/secrets/`

**Variables requises:**
- [ ] `DOCKER_REGISTRY_URL`
- [ ] `DOCKER_REGISTRY_USERNAME`
- [ ] `DOCKER_REGISTRY_PASSWORD`
- [ ] `DEPLOY_HOST` (02 Switch IP/hostname)
- [ ] `DEPLOY_USER` (SSH user)
- [ ] `DEPLOY_KEY` (SSH private key)
- [ ] `PROD_ENV_FILE` (encrypted `.env.prod`)

---

### 4.3 Deployment Script
**Fichier:** `deployment/deploy.sh`

**Objectives:**
- [ ] SSH connection à 02 Switch
- [ ] Pull latest images
- [ ] Backup data (optional)
- [ ] docker-compose down (graceful)
- [ ] docker-compose up -d (prod)
- [ ] Wait for health checks
- [ ] Rollback on failure

---

## Phase 5: Pipeline CI/CD Complète

### 5.1 Workflow: Complete Deploy
**Fichier:** `.github/workflows/deploy.yml`

**Trigger:** `push` sur `main` (production), `push` sur `dev` (staging)

**Jobs sequentiels:**
1. Build & Test (réuse workflow 3.1)
2. Docker Build & Push (réuse workflow 3.2)
3. Integration Tests (réuse workflow 3.3)
4. Deploy to 02 Switch (appelle deploy.sh)
5. Health checks post-deploy
6. Notification Slack/Email

**Features:**
- [ ] Approval gate pour prod (si besoin)
- [ ] Rollback automatique on health check failure
- [ ] Deployment notifications
- [ ] Release notes auto-generation

---

### 5.2 Monitoring & Logging
**Fichier:** `deployment/monitoring.yml`

**Objectives:**
- [ ] Prometheus scrape metrics
- [ ] ELK stack (optional)
- [ ] Alertes health check fails
- [ ] Log aggregation

---

## 📊 Dépendances & Ordre d'exécution

```
1. Phase 1 (Dockerfiles)
   ├─ Backend Dockerfile
   ├─ Frontend Dockerfile
   └─ MySQL schema validation
        ↓
2. Phase 2 (Docker Compose)
   ├─ docker-compose.yml (dev)
   ├─ docker-compose.prod.yml
   ├─ .env files
   └─ Test local `docker-compose up`
        ↓
3. Phase 3 (GitHub Actions)
   ├─ Build & Test workflow
   ├─ Docker Build & Push workflow
   ├─ Integration Tests workflow
   └─ Test manuellement sur branch
        ↓
4. Phase 4 (Deployment Config)
   ├─ 02 Switch access & setup
   ├─ Secrets configuration
   └─ Deploy script test
        ↓
5. Phase 5 (Full Pipeline)
   ├─ Deploy workflow
   ├─ Monitoring setup
   └─ End-to-end test
```

---

## 🎯 Checklist de Validation

### Phase 1
- [ ] Backend Dockerfile builds sans erreur
- [ ] Frontend Dockerfile builds sans erreur
- [ ] MySQL container démarre avec schema
- [ ] Health checks fonctionnent

### Phase 2
- [ ] `docker-compose up` démarre tous les services
- [ ] Frontend accessible sur `http://localhost`
- [ ] Backend accessible sur `http://localhost:8080`
- [ ] MySQL accessible depuis app
- [ ] Hot-reload fonctionne en dev

### Phase 3
- [ ] Tous les workflows run sans erreur sur `dev` branch
- [ ] Images pushées à Docker Registry
- [ ] Tagging correct (latest, dev, vX.X.X)

### Phase 4
- [ ] SSH access à 02 Switch ✓
- [ ] Docker installé sur 02 Switch ✓
- [ ] Registry credentials fonctionnent
- [ ] Deploy script exécutable

### Phase 5
- [ ] Pipeline complet exécuté sur merge to `main`
- [ ] App live sur 02 Switch
- [ ] Health checks passent
- [ ] Logs centralisés accessibles
- [ ] Rollback testé et validé

---

## 🔧 Stack Technique Recommandé

| Composant | Technologie | Notes |
|-----------|-------------|-------|
| Container runtime | Docker 24+ | |
| Registry | ghcr.io (GitHub) | Free, integré |
| Orchestration | Docker Compose | Simple, suffisant pour un serveur |
| Backend | Java 21 + Spring Boot 3.x | Existant |
| Frontend | Angular 18+ | Existant |
| Database | MySQL 8.0+ | Existant |
| CI/CD | GitHub Actions | Gratuit pour public repo |
| Monitoring | Prometheus + Grafana | Optional phase 2 |
| Logging | ELK / Loki | Optional phase 2 |

---

## 📝 Notes Importantes

1. **Secrets Management:** Jamais mettre `.env.prod` en git. Utiliser GitHub Secrets.
2. **Registry:** Décider public (ghcr.io) vs privé (Docker Hub paid)
3. **02 Switch:** Vérifier capacité CPU/RAM pour 3 containers en production
4. **Rollback:** Tester scénario de rollback avant de déployer en prod
5. **Backup:** MySQL data persistence sur 02 Switch (volumes, backups réguliers)

---

## 📅 Estimation par phase

| Phase | Tâches | Durée estimée | Notes |
|-------|--------|---------------|-------|
| 1 | 3 Dockerfiles | 4-6h | Multi-stage, optimisation |
| 2 | 2 compose files + env | 3-4h | Config, testing local |
| 3 | 3 workflows | 6-8h | Debug pipelines + registry |
| 4 | Setup 02 Switch | 2-3h | Si accès OK |
| 5 | Full integration | 4-5h | Testing, rollback |
| **TOTAL** | | **20-26h** | ~3 sprints |

---

**Next step:** Commencer par Phase 1 (Dockerfiles) ?
