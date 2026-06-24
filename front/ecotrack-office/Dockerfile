# ============================================================================
# EcoTrack Office - Frontend Dockerfile
# Multi-stage build for Angular application
# ============================================================================

# Stage 1: Build Angular app
FROM node:20-alpine AS builder

WORKDIR /build

# Copy package files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build Angular app (production mode with AOT)
RUN npm run build

# ============================================================================
# Stage 2: Serve with Nginx
FROM nginx:alpine

WORKDIR /usr/share/nginx/html

# Copy built app from builder
COPY --from=builder /build/dist/ecotrack-office/browser .

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /usr/share/nginx/html /var/cache/nginx /var/log/nginx /var/run

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Expose port
EXPOSE 8080

# Run Nginx
CMD ["nginx", "-g", "daemon off;"]
