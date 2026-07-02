#!/bin/sh
set -e

: "Check required env vars"
: "DB_HOST, DB_PORT (optional), DB_USER, DB_PASSWORD, DB_NAME"

DB_PORT=${DB_PORT:-3306}

echo "Testing DB connection to ${DB_HOST}:${DB_PORT} as ${DB_USER}..."

# Try a simple query
if mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASSWORD}" -e "SELECT 1;" "${DB_NAME}" >/dev/null 2>&1; then
  echo "Connection OK"
  exit 0
else
  echo "Connection FAILED"
  # show verbose attempt for logs
  mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" -p"${DB_PASSWORD}" -e "SELECT 1;" "${DB_NAME}" || true
  exit 2
fi
