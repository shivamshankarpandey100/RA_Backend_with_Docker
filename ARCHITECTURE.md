# reelApp - Microservices Architecture Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Services Details](#services-details)
3. [Technology Stack](#technology-stack)
4. [Docker Setup](#docker-setup)
5. [Running the Application](#running-the-application)
6. [API Documentation](#api-documentation)
7. [Database Schema](#database-schema)
8. [Troubleshooting](#troubleshooting)

---

## Architecture Overview

### Architecture Pattern
**Microservices Architecture with API Gateway Pattern**

```
┌─────────────────────────────────────────────────────────────┐
│                         Client                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ HTTPS/HTTP
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 3000)                  │
│              JWT Authentication & Authorization             │
│                    Request Routing & Proxy                  │
└─────────┬────────────┬──────────┬──────────┬────────────────┘
          │            │          │          │
          │            │          │          │
    ┌─────▼────┐  ┌───▼────┐ ┌──▼─────┐ ┌─▼──────────┐
    │ Catalog  │  │ Video  │ │ User   │ │Subscription│
    │ Service  │  │Ingest  │ │Service │ │  Service   │
    │ :8081    │  │ :8087  │ │ :8089  │ │   :8085    │
    └─────┬────┘  └───┬────┘ └───┬────┘ └─────┬──────┘
          │            │          │            │
          │            │          │            │
    ┌─────▼────┐  ┌───▼────┐ ┌───▼────┐ ┌─────▼──────┐
    │   DB:    │  │  DB:   │ │  DB:   │ │    DB:     │
    │newcatalog│  │ video_ │ │api_    │ │subscription│
    │          │  │ingestion│ │gateway │ │           │
    └──────────┘  └────────┘ └────────┘ └────────────┘
```

### Design Principles
- **Service Isolation**: Each service runs independently in its own container
- **Database Per Service**: Each service has its own PostgreSQL database
- **Single Entry Point**: All client requests go through the API Gateway
- **Stateless Services**: No shared state between services
- **Fault Tolerance**: If one service fails, others continue to operate

---

## Services Details

### 1. API Gateway (`RA_API_gateway`)
**Port**: 3000
**Database**: `api_gateway`

**Responsibilities**:
- User authentication (JWT-based)
- User registration and management
- Authorization and role-based access control
- Request routing to backend microservices
- Security enforcement
- Request/response logging

**Key Features**:
- JWT token generation and validation
- BCrypt password encryption
- Role-based access (SUPERADMIN, ADMIN, EDITOR, USER)
- WebClient-based HTTP proxying
- User context propagation via headers

**Public Endpoints**:
- `POST /api/auth/login` - User login
- `GET /health` - Health check

**Protected Endpoints**:
- `POST /api/auth/register` - Register user (SUPERADMIN only)
- `GET /api/auth/me` - Get current user
- `GET /api/auth/users` - List all users
- `PUT /api/auth/change-password` - Change password

**Proxy Routes**:
- `/catalog/**` → Catalog Service (8081)
- `/videos/**` → Video Ingestion Service (8087)
- `/users/**` → User Service (8089)
- `/streaming/**` → Streaming Service (8082)
- `/dashboard/**` → Dashboard Service (8084)
- `/stats/**` → Stats Service (8089)

---

### 2. Catalog Service (`RA_CatalogService`)
**Port**: 8081
**Database**: `newcatalog`

**Responsibilities**:
- Category/catalog management
- Content categorization
- Category search and filtering

**API Endpoints**:
- `POST /api/v1/categories` - Create category (ADMIN+)
- `GET /api/v1/categories` - List all categories (Public)
- `GET /api/v1/categories/search?name={name}` - Search categories
- `GET /api/v1/categories/{id}` - Get category details
- `PUT /api/v1/categories/{id}` - Update category (ADMIN+)
- `PATCH /api/v1/categories/{id}/status` - Update status (ADMIN+)
- `DELETE /api/v1/categories/{id}` - Delete category (SUPERADMIN/ADMIN)

**Entity Model**:
```java
Category {
  id: Long
  name: String
  description: String
  active: Boolean
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

---

### 3. Video Ingestion Service (`RA_VideoIngestionService`)
**Port**: 8087
**Database**: `video_ingestion`

**Responsibilities**:
- Video file uploads and storage
- Poster/thumbnail uploads
- Media metadata management
- File validation and processing

**Key Features**:
- Supports up to 2GB file uploads
- Local filesystem storage
- Storage paths: `storage/raw/videos/`, `storage/raw/posters/`
- Video language tracking
- File type validation

**API Endpoints**:
- `POST /api/media/upload` - Upload video or poster
  - Parameters:
    - `file`: MultipartFile
    - `type`: VIDEO | POSTER
    - `language`: String (required for VIDEO)

**Entity Models**:
```java
Video {
  id: Long
  language: String
  fileName: String
  contentType: String
  sizeMb: Double
  storagePath: String
  videoUrl: String
  createdAt: LocalDateTime
}

Poster {
  id: Long
  fileName: String
  contentType: String
  sizeMb: Double
  storagePath: String
  posterUrl: String
  createdAt: LocalDateTime
}
```

---

### 4. Subscription Service (`Saanvitechs-RA_subscriptionAPI`)
**Port**: 8085 (needs configuration)
**Database**: `subscripption`

**Responsibilities**:
- Subscription plan management
- Pricing and billing configuration
- Feature access control

**API Endpoints**:
- `GET /api/subscriptionPlans/getall` - Get all plans
- `GET /api/subscriptionPlans/id/{id}` - Get plan by ID
- `POST /api/subscriptionPlans/plan` - Create plan
- `PUT /api/subscriptionPlans/update/{id}` - Update plan
- `DELETE /api/subscriptionPlans/id/{id}` - Delete plan

**Entity Model**:
```java
SubsEntity {
  id: Long
  title: String
  durationMonths: Integer
  pricePaise: Long
  currency: String
  isMostPopular: Boolean
  savingsPercent: Integer
  trialDays: Integer
  trialPricePaise: Long
  features: List<String>
  isEnabled: Boolean
}
```

---

### 5. Auth Service (`RA_AuthService`)
**Status**: Placeholder - Not implemented
**Note**: Authentication is currently handled by the API Gateway

---

## Technology Stack

### Backend Services
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.9 / 4.0.1
- **Build Tool**: Maven 3.9.9
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security + JWT (JJWT 0.12.3)
- **HTTP Client**: Spring WebFlux WebClient
- **Utilities**: Lombok

### Database
- **DBMS**: PostgreSQL 15
- **Connection**: localhost:5432
- **Credentials**: postgres / root
- **Schema Management**: JPA DDL Auto-update

### Containerization
- **Container Runtime**: Docker
- **Orchestration**: Docker Compose
- **Base Images**:
  - Java: eclipse-temurin:17-jdk-alpine
  - PostgreSQL: postgres:15-alpine

---

## Docker Setup

### Prerequisites
- Docker Engine 20.10+
- Docker Compose 2.0+
- Git
- 8GB+ RAM recommended
- 20GB+ free disk space

### Directory Structure
```
reelApp/
├── docker-compose.yml          # Main orchestration file
├── .env                        # Environment variables
├── ARCHITECTURE.md             # This file
├── RA_API_gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── RA_CatalogService/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── RA_VideoIngestionService/
│   └── videoIngestion/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/
├── Saanvitechs-RA_subscriptionAPI/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
└── volumes/                    # Docker volumes for data persistence
    ├── postgres-gateway/
    ├── postgres-catalog/
    ├── postgres-video/
    ├── postgres-subscription/
    └── video-storage/
```

---

## Running the Application

### Step 1: Clone All Repositories
Since each service has its own GitHub repository, clone them into the `reelApp` folder:

```bash
# Create main directory
mkdir reelApp
cd reelApp

# Clone each service repository
git clone <RA_API_gateway-repo-url> RA_API_gateway
git clone <RA_CatalogService-repo-url> RA_CatalogService
git clone <RA_VideoIngestionService-repo-url> RA_VideoIngestionService
git clone <RA_subscriptionAPI-repo-url> Saanvitechs-RA_subscriptionAPI
```

### Step 2: Create Environment File
Create a `.env` file in the root `reelApp` directory:

```bash
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root

# API Gateway
GATEWAY_PORT=3000
GATEWAY_DB_NAME=api_gateway
JWT_SECRET=your-secret-key-change-this-in-production

# Catalog Service
CATALOG_PORT=8081
CATALOG_DB_NAME=newcatalog

# Video Ingestion Service
VIDEO_PORT=8087
VIDEO_DB_NAME=video_ingestion
VIDEO_STORAGE_PATH=/app/storage

# Subscription Service
SUBSCRIPTION_PORT=8085
SUBSCRIPTION_DB_NAME=subscripption

# Service URLs (for gateway routing)
CATALOG_SERVICE_URL=http://catalog-service:8081
VIDEO_SERVICE_URL=http://video-service:8087
SUBSCRIPTION_SERVICE_URL=http://subscription-service:8085
```

### Step 3: Build Docker Images
From the `reelApp` root directory:

```bash
# Build all services
docker-compose build

# Or build specific service
docker-compose build api-gateway
docker-compose build catalog-service
docker-compose build video-service
docker-compose build subscription-service
```

### Step 4: Start All Services
```bash
# Start all services in detached mode
Video Upload API (/videos/api/media/upload) - is this giving 403?
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f api-gateway
```

### Step 5: Verify Services are Running
```bash
# Check running containers
docker-compose ps

# Expected output:
# NAME                    STATUS              PORTS
# api-gateway            Up                  0.0.0.0:3000->3000/tcp
# catalog-service        Up                  0.0.0.0:8081->8081/tcp
# video-service          Up                  0.0.0.0:8087->8087/tcp
# subscription-service   Up                  0.0.0.0:8085->8085/tcp
# postgres-gateway       Up                  5432/tcp
# postgres-catalog       Up                  5432/tcp
# postgres-video         Up                  5432/tcp
# postgres-subscription  Up                  5432/tcp
```

### Step 6: Health Check
```bash
# Check API Gateway
curl http://localhost:3000/health

# Expected response: {"status":"UP"}
```

### Step 7: Create First User
```bash
# This requires updating the API Gateway to allow first user registration
# Or manually insert a SUPERADMIN user into the database
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@reelapp.com",
    "password": "Admin@123",
    "name": "Super Admin",
    "role": "SUPERADMIN"
  }'
```

### Step 8: Login and Get JWT Token
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@reelapp.com",
    "password": "Admin@123"
  }'

# Response will contain JWT token
# Use this token in subsequent requests:
# Authorization: Bearer <your-token>
```

---

## Common Docker Commands

### Service Management
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: Deletes all data)
docker-compose down -v

# Restart a specific service
docker-compose restart api-gateway

# Rebuild and restart a service
docker-compose up -d --build api-gateway

# Scale a service (run multiple instances)
docker-compose up -d --scale catalog-service=3
```

### Logs and Debugging
```bash
# View real-time logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f catalog-service

# View last 100 lines
docker-compose logs --tail=100

# Execute commands in running container
docker-compose exec api-gateway bash
docker-compose exec postgres-gateway psql -U postgres
```

### Database Access
```bash
# Connect to Gateway database
docker-compose exec postgres-gateway psql -U postgres -d api_gateway

# Connect to Catalog database
docker-compose exec postgres-catalog psql -U postgres -d newcatalog

# Backup database
docker-compose exec postgres-gateway pg_dump -U postgres api_gateway > backup.sql

# Restore database
docker-compose exec -T postgres-gateway psql -U postgres api_gateway < backup.sql
```

---

## API Documentation

### Authentication Flow

1. **Register User** (SUPERADMIN only)
```bash
POST /api/auth/register
Content-Type: application/json
Authorization: Bearer <superadmin-token>

{
  "email": "user@example.com",
  "password": "Password@123",
  "name": "John Doe",
  "role": "EDITOR"
}
```

2. **Login**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password@123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "EDITOR"
  }
}
```

3. **Access Protected Endpoint**
```bash
GET /api/auth/me
Authorization: Bearer <your-jwt-token>
```

### Catalog Service Examples

```bash
# Create Category
POST http://localhost:3000/catalog/api/v1/categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Action Movies",
  "description": "High-octane action films",
  "active": true
}

# Get All Categories (Public)
GET http://localhost:3000/catalog/api/v1/categories

# Search Categories
GET http://localhost:3000/catalog/api/v1/categories/search?name=Action
```

### Video Upload Examples

```bash
# Upload Video
POST http://localhost:3000/videos/api/media/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <video-file>
type: VIDEO
language: English

# Upload Poster
POST http://localhost:3000/videos/api/media/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <image-file>
type: POSTER
```

### Subscription Service Examples

```bash
# Get All Plans
GET http://localhost:3000/subscription/api/subscriptionPlans/getall

# Create Plan
POST http://localhost:3000/subscription/api/subscriptionPlans/plan
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Premium Monthly",
  "durationMonths": 1,
  "pricePaise": 99900,
  "currency": "INR",
  "isMostPopular": true,
  "savingsPercent": 0,
  "trialDays": 7,
  "trialPricePaise": 0,
  "features": ["HD Streaming", "No Ads", "Download"],
  "isEnabled": true
}
```

---

## Database Schema

### Gateway Database (`api_gateway`)

**users** table:
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255),
  role VARCHAR(50) NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Catalog Database (`newcatalog`)

**categories** table:
```sql
CREATE TABLE categories (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Video Ingestion Database (`video_ingestion`)

**videos** table:
```sql
CREATE TABLE videos (
  id BIGSERIAL PRIMARY KEY,
  language VARCHAR(100) NOT NULL,
  file_name VARCHAR(500),
  content_type VARCHAR(100),
  size_mb DECIMAL(10,2),
  storage_path VARCHAR(1000),
  video_url VARCHAR(1000),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**posters** table:
```sql
CREATE TABLE posters (
  id BIGSERIAL PRIMARY KEY,
  file_name VARCHAR(500),
  content_type VARCHAR(100),
  size_mb DECIMAL(10,2),
  storage_path VARCHAR(1000),
  poster_url VARCHAR(1000),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Subscription Database (`subscripption`)

**subs_entity** table:
```sql
CREATE TABLE subs_entity (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255),
  duration_months INTEGER,
  price_paise BIGINT,
  currency VARCHAR(10),
  is_most_popular BOOLEAN,
  savings_percent INTEGER,
  trial_days INTEGER,
  trial_price_paise BIGINT,
  features TEXT[], -- Array of features
  is_enabled BOOLEAN
);
```

---

## Troubleshooting

### Issue: Services can't connect to database

**Solution**:
```bash
# Check if PostgreSQL containers are running
docker-compose ps | grep postgres

# Check database logs
docker-compose logs postgres-gateway

# Ensure services are on same network
docker network ls
docker network inspect reelapp_default
```

### Issue: Port already in use

**Solution**:
```bash
# Find process using port
netstat -ano | findstr :3000  # Windows
lsof -i :3000                  # Linux/Mac

# Change port in .env file or docker-compose.yml
```

### Issue: Service crashes on startup

**Solution**:
```bash
# View service logs
docker-compose logs api-gateway

# Check if database is ready
docker-compose exec postgres-gateway pg_isready

# Rebuild service
docker-compose up -d --build api-gateway
```

### Issue: Cannot upload large videos

**Solution**:
- Check `spring.servlet.multipart.max-file-size` in application.properties
- Ensure Docker has enough disk space
- Increase NGINX/proxy timeouts if applicable

### Issue: JWT token expired

**Solution**:
- Login again to get new token
- Token expiry is set to 24 hours by default
- Change `jwt.expiration` in API Gateway properties

### Issue: Service can't find other services

**Solution**:
```bash
# Ensure services use container names, not localhost
# Check RouteConfig.java and update URLs to use Docker service names
# Example: http://catalog-service:8081 instead of http://localhost:8081
```

---

## Production Considerations

### Security
- [ ] Change default database passwords
- [ ] Use environment-specific JWT secrets
- [ ] Enable HTTPS/TLS
- [ ] Implement rate limiting
- [ ] Add API key authentication
- [ ] Enable CORS properly
- [ ] Scan images for vulnerabilities

### Performance
- [ ] Add Redis for caching
- [ ] Implement connection pooling
- [ ] Add CDN for static content
- [ ] Enable database query optimization
- [ ] Add application metrics (Prometheus)
- [ ] Implement load balancing

### Monitoring
- [ ] Add health check endpoints for all services
- [ ] Implement centralized logging (ELK Stack)
- [ ] Add distributed tracing (Jaeger/Zipkin)
- [ ] Set up alerting (Grafana)
- [ ] Monitor container resources

### Data Management
- [ ] Implement database backups
- [ ] Add migration scripts (Flyway/Liquibase)
- [ ] Use object storage (S3/MinIO) for videos
- [ ] Implement data retention policies
- [ ] Add data encryption at rest

### High Availability
- [ ] Multi-instance deployment
- [ ] Database replication
- [ ] Service auto-scaling
- [ ] Circuit breakers (Resilience4j)
- [ ] Retry mechanisms
- [ ] Graceful shutdown handling

---

## Next Steps

1. **Complete Missing Services**: Implement User, Streaming, and Dashboard services
2. **Add Service Discovery**: Consider Spring Cloud Netflix Eureka
3. **Implement Message Queue**: Add RabbitMQ/Kafka for async communication
4. **API Documentation**: Add Swagger/OpenAPI specs
5. **Testing**: Add integration and E2E tests
6. **CI/CD Pipeline**: Set up GitHub Actions/Jenkins
7. **Move to K8s**: For production scalability

---

## Support & Contact

For issues and questions:
- GitHub Issues: [Create issue in respective service repo]
- Documentation: This file
- Team Lead: [Contact information]

---

**Last Updated**: December 2024
**Version**: 1.0.0
**Architecture**: Microservices with Docker Compose
