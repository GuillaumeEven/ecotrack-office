#!/bin/bash

# ============================================================================
# EcoTrack Office - Deployment Script
# Executed on 02 Switch server
# ============================================================================

set -e

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================================================
# Configuration
# ============================================================================
REGISTRY="${1:-ghcr.io}"
BACKEND_IMAGE="${2:-guillaumeeven/ecotrack-office/ecotrack-backend}"
FRONTEND_IMAGE="${3:-guillaumeeven/ecotrack-office/ecotrack-frontend}"
VERSION="${4:-latest}"
ENVIRONMENT="${5:-staging}"

DEPLOYMENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_DIR="$DEPLOYMENT_DIR/backups"
LOG_FILE="$DEPLOYMENT_DIR/deployment.log"

# ============================================================================
# Functions
# ============================================================================
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
    exit 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

# ============================================================================
# Pre-deployment checks
# ============================================================================
log "=== Starting deployment to $ENVIRONMENT ==="
log "Backend Image: $REGISTRY/$BACKEND_IMAGE:$VERSION"
log "Frontend Image: $REGISTRY/$FRONTEND_IMAGE:$VERSION"

# Check Docker is running
if ! docker ps &>/dev/null; then
    error "Docker daemon is not running"
fi
log "✓ Docker daemon is running"

# Check docker-compose
if ! command -v docker-compose &>/dev/null; then
    error "docker-compose is not installed"
fi
log "✓ docker-compose is installed"

# Check required files
if [[ ! -f "$DEPLOYMENT_DIR/docker-compose.prod.yml" ]]; then
    error "docker-compose.prod.yml not found in $DEPLOYMENT_DIR"
fi
log "✓ docker-compose.prod.yml found"

if [[ ! -f "$DEPLOYMENT_DIR/.env.prod" ]]; then
    error ".env.prod not found in $DEPLOYMENT_DIR"
fi
log "✓ .env.prod found"

# ============================================================================
# Create backup
# ============================================================================
log "=== Creating backup ==="
mkdir -p "$BACKUP_DIR"

if bash "$DEPLOYMENT_DIR/backup.sh"; then
    log "✓ Backup completed successfully"
else
    warning "Backup failed, but continuing with deployment"
fi

# ============================================================================
# Pull latest images
# ============================================================================
log "=== Pulling Docker images ==="

docker pull "$REGISTRY/$BACKEND_IMAGE:$VERSION" || error "Failed to pull backend image"
log "✓ Backend image pulled"

docker pull "$REGISTRY/$FRONTEND_IMAGE:$VERSION" || error "Failed to pull frontend image"
log "✓ Frontend image pulled"

# ============================================================================
# Stop current services (graceful)
# ============================================================================
log "=== Stopping current services ==="

cd "$DEPLOYMENT_DIR"

if docker-compose -f docker-compose.prod.yml ps 2>/dev/null | grep -q "Up"; then
    log "Stopping services gracefully..."
    docker-compose -f docker-compose.prod.yml down --timeout=30
    log "✓ Services stopped"
else
    log "No running services to stop"
fi

sleep 5

# ============================================================================
# Start new services
# ============================================================================
log "=== Starting new services ==="

export DOCKER_REGISTRY="$REGISTRY/"
export VERSION="$VERSION"

docker-compose -f docker-compose.prod.yml up -d || error "Failed to start services"
log "✓ Services started"

# ============================================================================
# Wait for health checks
# ============================================================================
log "=== Waiting for services to be healthy ==="

MAX_RETRIES=60
RETRY_COUNT=0

# Check MySQL
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker-compose -f docker-compose.prod.yml exec -T mysql mysqladmin ping -h localhost &>/dev/null; then
        log "✓ MySQL is healthy"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        error "MySQL health check failed after $MAX_RETRIES attempts"
    fi
    sleep 2
done

RETRY_COUNT=0

# Check Backend
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker-compose -f docker-compose.prod.yml exec -T backend curl -f http://localhost:8080/actuator/health &>/dev/null; then
        log "✓ Backend is healthy"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        error "Backend health check failed after $MAX_RETRIES attempts"
    fi
    sleep 2
done

RETRY_COUNT=0

# Check Frontend
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker-compose -f docker-compose.prod.yml exec -T frontend wget --quiet --tries=1 --spider http://localhost:8080/health &>/dev/null; then
        log "✓ Frontend is healthy"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        error "Frontend health check failed after $MAX_RETRIES attempts"
    fi
    sleep 2
done

# ============================================================================
# Cleanup
# ============================================================================
log "=== Cleaning up ==="

# Remove dangling images
docker image prune -f &>/dev/null || true
log "✓ Cleaned up dangling images"

# ============================================================================
# Deployment complete
# ============================================================================
log ""
log "╔════════════════════════════════════════╗"
log "║  ✅ DEPLOYMENT SUCCESSFUL              ║"
log "║  Environment: $ENVIRONMENT"
log "║  Version: $VERSION"
log "║  Timestamp: $(date +'%Y-%m-%d %H:%M:%S')"
log "╚════════════════════════════════════════╝"
log ""
log "Access the application:"
log "  Frontend: http://$(hostname -I | awk '{print $1}')"
log "  Backend API: http://$(hostname -I | awk '{print $1}'):8080"
log ""

exit 0
