# ReelApp - Startup & API Testing Guide

## Architecture Overview

```
┌──────────┐     1. Login        ┌──────────────────────┐
│  Client  │ ──────────────────► │ Auth Service (8080)  │
│          │◄─────────────────── │ JWT with role claim  │
└──────────┘   JWT Token         └──────────────────────┘
     │
     │ 2. Request + JWT
     ▼
┌─────────────────────────────────────────────────────────┐
│               API Gateway (3000)                         │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │ JWT Validation  │───►│ Permission Check Filter     │ │
│  │    Filter       │    │ (calls Permission Service)  │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────┬───────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        ▼                         ▼                         ▼
┌───────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ Catalog Svc   │       │ User Service    │       │ Video Service   │
│   (8081)      │       │   (8089)        │       │    (8087)       │
└───────────────┘       └─────────────────┘       └─────────────────┘
```

## Service Ports

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 3000 | Central entry point for all requests |
| Auth Service | 8080 | Authentication & JWT generation |
| Permission Service | 8085 | IAM - Role-based access control |
| Catalog Service | 8081 | Categories, Content, Carousels |
| User Service | 8089 | User management |
| Video Service | 8087 | Video/Poster uploads |

## Database Ports

| Database | Port | Database Name |
|----------|------|---------------|
| postgres-auth | 5432 | auth_db |
| postgres-catalog | 5433 | newcatalog |
| postgres-user | 5434 | admin-userservice |
| postgres-video | 5435 | video_ingestion |
| postgres-permission | 5436 | permission_access |

---

## Step 1: Start All Services

```bash
# Navigate to the project directory
cd C:\Users\Shreyansh\reelApp

# Build and start all services (first time takes longer)
docker-compose up --build

# Or run in detached mode (background)
docker-compose up --build -d
```

## Step 2: Verify Services Are Running

```bash
# Check all containers are healthy
docker-compose ps
```

**Expected output** - all services should show `healthy`:
```
NAME                  STATUS                   PORTS
api-gateway           Up (healthy)             0.0.0.0:3000->3000/tcp
auth-service          Up (healthy)             0.0.0.0:8080->8080/tcp
catalog-service       Up (healthy)             0.0.0.0:8081->8081/tcp
permission-service    Up (healthy)             0.0.0.0:8085->8085/tcp
user-service          Up (healthy)             0.0.0.0:8089->8089/tcp
video-service         Up (healthy)             0.0.0.0:8087->8087/tcp
postgres-auth         Up (healthy)             0.0.0.0:5432->5432/tcp
postgres-catalog      Up (healthy)             0.0.0.0:5433->5432/tcp
postgres-permission   Up (healthy)             0.0.0.0:5436->5432/tcp
postgres-user         Up (healthy)             0.0.0.0:5434->5432/tcp
postgres-video        Up (healthy)             0.0.0.0:5435->5432/tcp
```

## Step 3: Test Health Endpoints

```bash
# API Gateway health
curl http://localhost:3000/actuator/health

# Auth Service health
curl http://localhost:8080/actuator/health

# Permission Service health
curl http://localhost:8085/actuator/health
```

---

# API Testing Flow

## Test 1: Register a New User

```bash
curl -X POST http://localhost:3000/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Admin",
    "username": "testadmin",
    "email": "admin@test.com",
    "phone": "1234567890",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "userId": "us_001",
  "message": "User registered successfully"
}
```
Run this command to make that user admin
docker exec postgres-auth psql -U postgres -d auth_db -c "UPDATE users SET role='ADMIN' WHERE email='admin@example.com';"
## Test 2: Login and Get JWT Token

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "us_001",
    "name": "Test Admin",
    "email": "admin@test.com",
    "role": "USER"
  },
  "message": "Login successful"
}
```

> **Important:** Save the token for subsequent requests.

## Test 3: Access Protected Endpoint (With Token)

```bash
# Replace <YOUR_TOKEN> with the actual token from login response
curl -X GET http://localhost:3000/api/v1/categories \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

**Expected Response:** (Categories list or empty array)
```json
[]
```

## Test 4: Access Protected Endpoint (Without Token)

```bash
curl -X GET http://localhost:3000/api/v1/categories
```

**Expected Response:**
```json
{
  "error": "Missing Authorization header",
  "status": 401
}
```

## Test 5: Test Permission Denied (USER Role Creating Content)

With a USER role token, try to create a category (requires ADMIN/EDITOR):

```bash
curl -X POST http://localhost:3000/api/v1/categories \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Category",
    "description": "Test description"
  }'
```

**Expected Response (if role is USER):**
```json
{
  "error": "Role 'USER' does not have permission for this endpoint",
  "status": 403
}
```

---

# Testing with Different Roles

## Default Role Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full access to all endpoints (CRUD on all resources) |
| **EDITOR** | Content, categories, carousels, draft-content, media upload |
| **USER** | Read-only access to content, categories, carousels, media |

## Create an ADMIN User (Direct DB Update)

Connect to the auth database and update the role:

```bash
# Connect to auth database
docker exec -it postgres-auth psql -U postgres -d auth_db

# Update user role to ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@test.com';

# Exit
\q
```

Now login again to get a new token with ADMIN role:

```bash
# Login again
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "password123"
  }'
```

## Test ADMIN Permissions

```bash
# Create category with ADMIN token
curl -X POST http://localhost:3000/api/v1/categories \
  -H "Authorization: Bearer <NEW_ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Movies",
    "description": "Movie content"
  }'
```

**Expected Response:** Success (201 Created)

---

# Quick Test Script (PowerShell)

Save this as `test-api.ps1` and run with PowerShell:

```powershell
$baseUrl = "http://localhost:3000"

# 1. Register user
Write-Host "1. Registering user..." -ForegroundColor Yellow
$signup = Invoke-RestMethod -Uri "$baseUrl/api/auth/signup" -Method POST -ContentType "application/json" -Body '{"name":"Test User","username":"testuser","email":"test@example.com","phone":"9876543210","password":"test123"}'
Write-Host "Response: $($signup | ConvertTo-Json)" -ForegroundColor Green

# 2. Login
Write-Host "`n2. Logging in..." -ForegroundColor Yellow
$login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"test@example.com","password":"test123"}'
$token = $login.token
Write-Host "Token received: $($token.Substring(0,50))..." -ForegroundColor Green

# 3. Access protected endpoint
Write-Host "`n3. Accessing categories..." -ForegroundColor Yellow
$headers = @{ "Authorization" = "Bearer $token" }
try {
    $categories = Invoke-RestMethod -Uri "$baseUrl/api/v1/categories" -Method GET -Headers $headers
    Write-Host "Categories: $($categories | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Test without token
Write-Host "`n4. Testing without token (should fail)..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/v1/categories" -Method GET
} catch {
    Write-Host "Expected error: Unauthorized" -ForegroundColor Green
}
```

---

# Quick Test Script (Bash)

Save this as `test-api.sh` and run with Git Bash or WSL:

```bash
#!/bin/bash

BASE_URL="http://localhost:3000"

echo "=========================================="
echo "1. Registering user..."
echo "=========================================="
curl -s -X POST "$BASE_URL/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","username":"testuser2","email":"test2@example.com","phone":"9876543211","password":"test123"}' | jq .

echo ""
echo "=========================================="
echo "2. Logging in..."
echo "=========================================="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test2@example.com","password":"test123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: ${TOKEN:0:50}..."

echo ""
echo "=========================================="
echo "3. Accessing protected endpoint (with token)..."
echo "=========================================="
curl -s -X GET "$BASE_URL/api/v1/categories" \
  -H "Authorization: Bearer $TOKEN" | jq .

echo ""
echo "=========================================="
echo "4. Testing without token (should fail)..."
echo "=========================================="
curl -s -X GET "$BASE_URL/api/v1/categories" | jq .

echo ""
echo "=========================================="
echo "5. Testing POST without permission (USER role)..."
echo "=========================================="
curl -s -X POST "$BASE_URL/api/v1/categories" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","description":"Test"}' | jq .
```

---

# Useful Docker Commands

```bash
# View logs for specific service
docker-compose logs -f api-gateway
docker-compose logs -f auth-service
docker-compose logs -f permission-service

# View all logs
docker-compose logs -f

# Restart a specific service
docker-compose restart api-gateway

# Stop all services
docker-compose down

# Stop and remove volumes (clean start)
docker-compose down -v

# Rebuild specific service
docker-compose up --build -d api-gateway
```

---

# Database Commands

```bash
# Check permission service seeded data
docker exec -it postgres-permission psql -U postgres -d permission_access \
  -c "SELECT path_pattern, http_method, allowed_roles FROM endpoint_permissions LIMIT 10;"

# Check roles
docker exec -it postgres-permission psql -U postgres -d permission_access \
  -c "SELECT name, description, active FROM roles;"

# Check registered users
docker exec -it postgres-auth psql -U postgres -d auth_db \
  -c "SELECT id, email, role, status FROM users;"

# Update user role to ADMIN
docker exec -it postgres-auth psql -U postgres -d auth_db \
  -c "UPDATE users SET role = 'ADMIN' WHERE email = 'admin@test.com';"
```

---

# API Endpoints Reference

## Auth Service (Public - No Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |
| POST | `/api/auth/forgot-password` | Request password reset OTP |
| POST | `/api/auth/reset-password` | Reset password with OTP |
| POST | `/api/auth/change-password` | Change password |

## Catalog Service (Protected)

| Method | Endpoint | Allowed Roles |
|--------|----------|---------------|
| GET | `/api/v1/categories` | ADMIN, EDITOR, USER |
| GET | `/api/v1/categories/{id}` | ADMIN, EDITOR, USER |
| POST | `/api/v1/categories` | ADMIN, EDITOR |
| PUT | `/api/v1/categories/{id}` | ADMIN, EDITOR |
| DELETE | `/api/v1/categories/{id}` | ADMIN |
| GET | `/api/v1/content` | ADMIN, EDITOR, USER |
| POST | `/api/v1/content` | ADMIN, EDITOR |
| GET | `/api/v1/carousels` | ADMIN, EDITOR, USER |
| POST | `/api/v1/carousels` | ADMIN, EDITOR |

## User Service (Protected - ADMIN Only)

| Method | Endpoint | Allowed Roles |
|--------|----------|---------------|
| GET | `/api/v1/users` | ADMIN |
| GET | `/api/v1/users/{id}` | ADMIN |
| POST | `/api/v1/users` | ADMIN |
| PUT | `/api/v1/users/{id}` | ADMIN |
| DELETE | `/api/v1/users/{id}` | ADMIN |

## Video Service (Protected)

| Method | Endpoint | Allowed Roles |
|--------|----------|---------------|
| GET | `/api/media/**` | ADMIN, EDITOR, USER |
| POST | `/api/media/upload` | ADMIN, EDITOR |
| DELETE | `/api/media/**` | ADMIN |

## Permission Service (Protected - ADMIN Only)

| Method | Endpoint | Allowed Roles |
|--------|----------|---------------|
| GET | `/api/roles` | ADMIN |
| POST | `/api/roles` | ADMIN |
| PUT | `/api/roles/{id}` | ADMIN |
| GET | `/api/permissions/check` | Internal (Gateway) |

---

# Troubleshooting

| Issue | Solution |
|-------|----------|
| Service not starting | Check logs: `docker-compose logs <service-name>` |
| Database connection error | Wait for DB health check, or restart: `docker-compose restart <service>` |
| Port already in use | Stop existing services or change ports in docker-compose.yml |
| JWT validation failed | Ensure same `JWT_SECRET` in auth-service and api-gateway |
| Permission denied unexpectedly | Check `endpoint_permissions` table has correct entries |
| Build fails | Clear Docker cache: `docker-compose build --no-cache` |
| Container keeps restarting | Check logs for startup errors |

---

# Environment Variables

You can customize the following in `docker-compose.yml` or create a `.env` file:

```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root

# JWT
JWT_SECRET=afkjh3r9238hf92h3f983hf9283hf9823h9f82h3f9823hf9283hf9823h9f82h3f9823h620ra120hul4954

# Ports
GATEWAY_PORT=3000
AUTH_PORT=8080
PERMISSION_PORT=8085
CATALOG_PORT=8081
USER_PORT=8089
VIDEO_PORT=8087

# Logging
SHOW_SQL=false
```
