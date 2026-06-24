#!/bin/bash

# ============================================================================
# EcoTrack Office - Backup Script
# Backs up MySQL database before deployment
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
DEPLOYMENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKUP_DIR="$DEPLOYMENT_DIR/backups"
BACKUP_RETENTION_DAYS=7
LOG_FILE="$DEPLOYMENT_DIR/backup.log"

# ============================================================================
# Functions
# ============================================================================
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
    return 1
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

# ============================================================================
# Create backup directory
# ============================================================================
mkdir -p "$BACKUP_DIR"
log "Backup directory: $BACKUP_DIR"

# ============================================================================
# Backup MySQL database
# ============================================================================
log "=== Starting MySQL backup ==="

cd "$DEPLOYMENT_DIR"

BACKUP_FILE="$BACKUP_DIR/ecotrack_db_backup_$(date +'%Y%m%d_%H%M%S').sql"

if docker-compose -f docker-compose.prod.yml ps mysql 2>/dev/null | grep -q "Up"; then
    log "Creating database dump..."

    # Get MySQL credentials from .env.prod
    DB_USER=$(grep "^DB_USERNAME=" .env.prod 2>/dev/null | cut -d= -f2 || echo "ecotrack_user")
    DB_PASSWORD=$(grep "^DB_PASSWORD=" .env.prod 2>/dev/null | cut -d= -f2 || echo "")
    DB_NAME=$(grep "^DB_NAME=" .env.prod 2>/dev/null | cut -d= -f2 || echo "ecotrack")

    if docker-compose -f docker-compose.prod.yml exec -T mysql \
        mysqldump -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "$BACKUP_FILE" 2>/dev/null; then
        log "✓ Database backup created: $BACKUP_FILE"

        # Compress the backup
        gzip "$BACKUP_FILE"
        BACKUP_FILE="$BACKUP_FILE.gz"
        log "✓ Backup compressed: $BACKUP_FILE"
    else
        error "Failed to create database backup"
        return 1
    fi
else
    warning "MySQL is not running, skipping database backup"
fi

# ============================================================================
# Backup volumes
# ============================================================================
log "=== Backing up Docker volumes ==="

VOLUME_BACKUP_DIR="$BACKUP_DIR/volumes_$(date +'%Y%m%d_%H%M%S')"
mkdir -p "$VOLUME_BACKUP_DIR"

if docker volume inspect ecotrack-office_mysql_data_prod &>/dev/null; then
    log "Creating volume backup..."
    docker run --rm \
        -v ecotrack-office_mysql_data_prod:/data \
        -v "$VOLUME_BACKUP_DIR":/backup \
        alpine:latest \
        tar czf /backup/mysql_data.tar.gz -C /data . 2>/dev/null || \
        warning "Failed to backup volume (it may be in use)"
    log "✓ Volume backup created"
else
    warning "Volume not found, skipping volume backup"
fi

# ============================================================================
# Cleanup old backups
# ============================================================================
log "=== Cleaning up old backups ==="

find "$BACKUP_DIR" -maxdepth 1 -type f -mtime +$BACKUP_RETENTION_DAYS -delete 2>/dev/null || true
find "$BACKUP_DIR" -maxdepth 1 -type d -mtime +$BACKUP_RETENTION_DAYS -exec rm -rf {} + 2>/dev/null || true

log "✓ Old backups cleaned up (retention: $BACKUP_RETENTION_DAYS days)"

# ============================================================================
# Backup summary
# ============================================================================
log ""
log "╔════════════════════════════════════════╗"
log "║  ✅ BACKUP COMPLETE                     ║"
log "║  Directory: $BACKUP_DIR"
log "║  Timestamp: $(date +'%Y-%m-%d %H:%M:%S')"
log "╚════════════════════════════════════════╝"

# Show backup files
log "Backup files:"
ls -lh "$BACKUP_DIR" 2>/dev/null | tail -n +2 | awk '{printf "  - %s (%s)\n", $9, $5}' || log "  No backup files found"

log ""
exit 0
