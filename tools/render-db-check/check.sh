#!/usr/bin/env bash
set -euo pipefail

: "Check required env vars"
: "DB_HOST, DB_PORT (optional), DB_USERNAME, DB_PASSWORD, DB_NAME"

DB_PORT=${DB_PORT:-3306}

echo "Testing DB connection to ${DB_HOST}:${DB_PORT} as ${DB_USERNAME:-<unset>}..."

echo "---- Render DB check: environment ----"
echo "DB_HOST=${DB_HOST:-<unset>}"
echo "DB_PORT=${DB_PORT:-3306}"
echo "DB_NAME=${DB_NAME:-<unset>}"
echo "DB_USERNAME=${DB_USERNAME:-<unset>}"
echo "DB_URL=${DB_URL:-<unset>}"
echo "(DB_PASSWORD is hidden)"
echo "---- end env ----"

echo "Detecting outbound public IP..."
if ip=$(curl -fsS https://ifconfig.me 2>/dev/null); then
  echo "$ip"
elif ip=$(curl -fsS https://ifconfig.co 2>/dev/null); then
  echo "$ip"
else
  echo "(no public IP detected)"
fi

if [ -z "${DB_HOST:-}" ]; then
  echo "ERROR: DB_HOST is not set. Aborting."
  exit 2
fi

echo "Testing mysql connection to ${DB_HOST}:${DB_PORT:-3306} as ${DB_USERNAME:-<unset>}..."

# Try multiple attempts with short timeout to detect intermittent failures
tmpout=$(mktemp)
for i in 1 2 3; do
  echo "Attempt $i: $(date -u +'%FT%T%z')"
  if mysql --connect-timeout=5 -h "${DB_HOST}" -P "${DB_PORT:-3306}" -u "${DB_USERNAME}" -p"${DB_PASSWORD:-}" -e "SELECT 1;" "${DB_NAME:-}" >"$tmpout" 2>&1; then
    echo "Connection OK"
    sed -n '1,200p' "$tmpout" || true
    rm -f "$tmpout"
    exit 0
  else
    rc=$?
    echo "Attempt $i failed (exit $rc). Output:"
    sed -n '1,200p' "$tmpout" || true
    # exponential backoff
    sleep $((i * 2))
  fi
done
echo "All attempts failed"
rm -f "$tmpout"
exit 2

# Try a simple query
