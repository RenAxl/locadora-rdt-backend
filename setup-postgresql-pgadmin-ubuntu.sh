#!/usr/bin/env bash
set -euo pipefail

DB_NAME="locadora-rdt"
DB_USER="postgres"
DB_PASSWORD="root"

echo "Installing PostgreSQL..."
sudo apt update
sudo apt install -y postgresql postgresql-contrib curl ca-certificates gnupg lsb-release

echo "Starting PostgreSQL service..."
sudo systemctl enable --now postgresql

echo "Configuring PostgreSQL user and database..."
sudo -u postgres psql -v ON_ERROR_STOP=1 <<SQL
ALTER USER ${DB_USER} WITH PASSWORD '${DB_PASSWORD}';
SELECT 'CREATE DATABASE "${DB_NAME}"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${DB_NAME}')\gexec
SQL

echo "Installing pgAdmin repository..."
sudo install -d -m 0755 /etc/apt/keyrings
curl -fsSL https://www.pgadmin.org/static/packages_pgadmin_org.pub \
  | sudo gpg --dearmor -o /etc/apt/keyrings/packages-pgadmin-org.gpg

echo "deb [signed-by=/etc/apt/keyrings/packages-pgadmin-org.gpg] https://ftp.postgresql.org/pub/pgadmin/pgadmin4/apt/$(lsb_release -cs) pgadmin4 main" \
  | sudo tee /etc/apt/sources.list.d/pgadmin4.list >/dev/null

echo "Installing pgAdmin desktop..."
sudo apt update
sudo apt install -y pgadmin4-desktop

echo "Validating PostgreSQL connection..."
PGPASSWORD="${DB_PASSWORD}" psql -h localhost -p 5432 -U "${DB_USER}" -d "${DB_NAME}" -c "SELECT current_database(), current_user;"

echo
echo "Done."
echo "PostgreSQL JDBC URL: jdbc:postgresql://localhost:5432/${DB_NAME}"
echo "Username: ${DB_USER}"
echo "Password: ${DB_PASSWORD}"
echo
echo "Open pgAdmin from the applications menu or run: pgadmin4"
