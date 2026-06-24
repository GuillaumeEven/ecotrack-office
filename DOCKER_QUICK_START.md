# ============================================================================
# EcoTrack Office - Docker Compose Quick Start Guide
# ============================================================================

## Prerequisites
- Docker 24.0+
- Docker Compose 2.20+
- Git

## Quick Start

### 1. Development Environment (Local)

```bash
# Clone the repository and navigate to root
cd ecotrack-office

# Load development environment
cp .env.dev .env

# Build and start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

Access the application:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **MySQL**: localhost:3306

### 2. Production Environment (02 Switch)

```bash
# Copy production environment (update with actual secrets)
cp .env.prod .env

# Update secrets from GitHub or environment variables
export DB_PASSWORD="your_secure_password"
export JWT_SECRET="your_jwt_secret"
# ... (set other required variables)

# Use production compose file
docker-compose -f docker-compose.prod.yml up -d

# Check status
docker-compose -f docker-compose.prod.yml ps
```

## Common Commands

```bash
# View service logs
docker-compose logs -f <service_name>

# Rebuild services
docker-compose build

# Force rebuild (no cache)
docker-compose build --no-cache

# Stop services (keep data)
docker-compose down

# Stop and remove volumes (DELETE DATA!)
docker-compose down -v

# Restart a service
docker-compose restart <service_name>

# Access MySQL shell
docker-compose exec mysql mysql -u ecotrack_user -p ecotrack

# Access backend container
docker-compose exec backend bash

# Execute command in container
docker-compose exec backend curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Database connection failed
- Check MySQL container is running: `docker-compose ps`
- Verify credentials in .env file
- Check logs: `docker-compose logs mysql`

### Frontend can't reach backend API
- Verify backend container is running and healthy
- Check `CORS_ALLOWED_ORIGINS` in .env
- Check Nginx proxy config: `front/ecotrack-office/nginx.conf`

### Port already in use
```bash
# Find what's using the port (e.g., 8080)
lsof -i :8080
# Kill the process
kill -9 <PID>
```

### Rebuild everything from scratch
```bash
docker-compose down -v
docker system prune -a
docker-compose up -d
```

## Volume & Data Persistence

- **MySQL data** persists in `mysql_data` volume
- **Prod MySQL data** persists in `mysql_data_prod` volume
- Remove with: `docker-compose down -v`

## Security Notes for Production

1. **Change JWT_SECRET** - Generate with: `openssl rand -base64 32`
2. **Use strong DB password** - Min 16 characters, mix of letters/numbers/symbols
3. **Update CORS_ALLOWED_ORIGINS** - Set to your actual domain
4. **Enable HTTPS** - Use a reverse proxy (nginx/traefik) in front
5. **Store secrets in GitHub Secrets** - Never commit .env.prod
6. **Regular backups** - Backup MySQL volume regularly

## Performance Tuning (Production)

- Adjust resource limits in `docker-compose.prod.yml`
- Monitor with: `docker stats`
- Scale services with Docker Swarm or Kubernetes (future)

## Next Steps

1. Test local development: `docker-compose up`
2. Run integration tests (Phase 3)
3. Push images to registry (Phase 3)
4. Deploy to 02 Switch (Phase 4)
5. Set up CI/CD pipeline (Phase 5)
