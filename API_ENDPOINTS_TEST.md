# API Gateway - Complete Endpoint Testing Guide

This document provides exact curl commands to test all endpoints in the Reel App API Gateway with SUPERADMIN credentials.

## Table of Contents
- [Prerequisites](#prerequisites)
- [1. Authentication](#1-authentication)
- [2. Catalog Service Endpoints](#2-catalog-service-endpoints)
- [3. User Service Endpoints](#3-user-service-endpoints)
- [4. Video Service Endpoints](#4-video-service-endpoints)
- [5. Streaming Service Endpoints](#5-streaming-service-endpoints)
- [6. Dashboard Service Endpoints](#6-dashboard-service-endpoints)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Environment Setup
- **API Gateway URL**: `http://localhost:3000`
- **Database**: PostgreSQL running on `localhost:5432`
- **Database Name**: `api_gateway`

### Superadmin Credentials
```json
{
  "email": "superadmin@apigateway.local",
  "password": "SuperAdmin@123"
}
```

### Important Notes
1. The superadmin user is automatically created on first application startup
2. All timestamps should be in ISO 8601 format
3. Replace `<TOKEN>` with your actual JWT token from login response
4. All file uploads use multipart/form-data encoding

---

## 1. Authentication

### 1.1 Login (Get JWT Token)
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "superadmin@apigateway.local",
    "password": "SuperAdmin@123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "superadmin@apigateway.local",
    "role": "SUPERADMIN",
    "name": "Super Administrator"
  }
}
```

**Save the token for subsequent requests:**
```bash
# Linux/Mac
export TOKEN="<paste_token_here>"

# Windows CMD
set TOKEN=<paste_token_here>

# Windows PowerShell
$TOKEN="<paste_token_here>"
```

### 1.2 Get Current User Info
```bash
curl -X GET http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

### 1.3 Change Password
```bash
curl -X POST http://localhost:3000/api/auth/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "SuperAdmin@123",
    "newPassword": "NewPassword@123"
  }'
```

### 1.4 Register New User (SUPERADMIN Only)
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "editor@apigateway.local",
    "password": "Editor@123",
    "role": "EDITOR",
    "name": "Content Editor"
  }'
```

**Note**: SUPERADMIN cannot register another SUPERADMIN. Valid roles: ADMIN, EDITOR, USER, GUEST

### 1.5 List All Users (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/api/auth/users \
  -H "Authorization: Bearer $TOKEN"
```

---

## 2. Catalog Service Endpoints

**Service Port**: 8081
**Route Prefix**: `/catalog`

### 2.1 Get All Catalog Items (Public)
```bash
curl -X GET http://localhost:3000/catalog/api/catalog
```

### 2.2 Get Catalog Item by ID (Public)
```bash
curl -X GET http://localhost:3000/catalog/api/catalog/<CATALOG_ID>
```

### 2.3 Create Catalog Item (SUPERADMIN/ADMIN/EDITOR)
```bash
curl -X POST http://localhost:3000/catalog/api/catalog \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Sample Movie",
    "description": "A sample movie description",
    "genre": "Action",
    "releaseYear": 2024,
    "duration": 120,
    "rating": 4.5
  }'
```

### 2.4 Update Catalog Item (SUPERADMIN/ADMIN/EDITOR)
```bash
curl -X PUT http://localhost:3000/catalog/api/catalog/<CATALOG_ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Movie Title",
    "description": "Updated description",
    "rating": 4.8
  }'
```

### 2.5 Partial Update Catalog Item (SUPERADMIN/ADMIN/EDITOR)
```bash
curl -X PATCH http://localhost:3000/catalog/api/catalog/<CATALOG_ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 5.0
  }'
```

### 2.6 Delete Catalog Item (SUPERADMIN/ADMIN Only)
```bash
curl -X DELETE http://localhost:3000/catalog/api/catalog/<CATALOG_ID> \
  -H "Authorization: Bearer $TOKEN"
```

---

## 3. User Service Endpoints

**Service Port**: 8089
**Route Prefix**: `/users`

### 3.1 Get All Users (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/users/api/users \
  -H "Authorization: Bearer $TOKEN"
```

### 3.2 Get User by ID (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/users/api/users/<USER_ID> \
  -H "Authorization: Bearer $TOKEN"
```

### 3.3 Create User (SUPERADMIN/ADMIN Only)
```bash
curl -X POST http://localhost:3000/users/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "Password@123",
    "role": "USER",
    "name": "New User",
    "isActive": true
  }'
```

### 3.4 Update User (SUPERADMIN/ADMIN/USER)
```bash
curl -X PUT http://localhost:3000/users/api/users/<USER_ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "email": "updated@example.com"
  }'
```

### 3.5 Partial Update User (SUPERADMIN/ADMIN/USER)
```bash
curl -X PATCH http://localhost:3000/users/api/users/<USER_ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "isActive": false
  }'
```

### 3.6 Delete User (SUPERADMIN/ADMIN Only)
```bash
curl -X DELETE http://localhost:3000/users/api/users/<USER_ID> \
  -H "Authorization: Bearer $TOKEN"
```

### 3.7 Get User Statistics (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/stats/api/stats \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. Video Service Endpoints

**Service Port**: 8087
**Route Prefix**: `/videos`

### 4.1 Upload Video File (SUPERADMIN/ADMIN/EDITOR)

**Important**: This is the main endpoint for video uploads that you mentioned having 403 errors with.

```bash
curl -X POST http://localhost:3000/videos/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/your/video.mp4" \
  -F "type=VIDEO" \
  -F "language=en"
```

**Parameters:**
- `file`: The video file (multipart file upload)
- `type`: Must be "VIDEO"
- `language`: Optional language code (en, es, fr, etc.)

**Example with curl (Windows):**
```cmd
curl -X POST http://localhost:3000/videos/api/media/upload ^
  -H "Authorization: Bearer %TOKEN%" ^
  -F "file=@C:\Videos\sample.mp4" ^
  -F "type=VIDEO" ^
  -F "language=en"
```

### 4.2 Upload Poster/Image (SUPERADMIN/ADMIN/EDITOR)

```bash
curl -X POST http://localhost:3000/videos/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/your/poster.jpg" \
  -F "type=POSTER"
```

**Parameters:**
- `file`: The image file (JPG, PNG, etc.)
- `type`: Must be "POSTER"

### 4.3 Get All Videos (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X GET http://localhost:3000/videos/api/media/videos \
  -H "Authorization: Bearer $TOKEN"
```

### 4.4 Get Video by ID (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X GET http://localhost:3000/videos/api/media/videos/<VIDEO_ID> \
  -H "Authorization: Bearer $TOKEN"
```

### 4.5 Get All Posters (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X GET http://localhost:3000/videos/api/media/posters \
  -H "Authorization: Bearer $TOKEN"
```

### 4.6 Get Poster by ID (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X GET http://localhost:3000/videos/api/media/posters/<POSTER_ID> \
  -H "Authorization: Bearer $TOKEN"
```

### 4.7 Update Video Metadata (SUPERADMIN/ADMIN/EDITOR)
```bash
curl -X PUT http://localhost:3000/videos/api/media/videos/<VIDEO_ID> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "language": "es",
    "status": "PROCESSED"
  }'
```

### 4.8 Delete Video (SUPERADMIN/ADMIN Only)
```bash
curl -X DELETE http://localhost:3000/videos/api/media/videos/<VIDEO_ID> \
  -H "Authorization: Bearer $TOKEN"
```

### 4.9 Delete Poster (SUPERADMIN/ADMIN Only)
```bash
curl -X DELETE http://localhost:3000/videos/api/media/posters/<POSTER_ID> \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. Streaming Service Endpoints

**Service Port**: 8082
**Route Prefix**: `/streaming`

### 5.1 Get Stream Info (Public)
```bash
curl -X GET http://localhost:3000/streaming/api/stream/<VIDEO_ID>
```

### 5.2 Start Streaming Session (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X POST http://localhost:3000/streaming/api/stream/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "videoId": "<VIDEO_ID>",
    "quality": "1080p"
  }'
```

### 5.3 Update Stream Settings (SUPERADMIN/ADMIN/EDITOR)
```bash
curl -X PUT http://localhost:3000/streaming/api/stream/settings \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "maxBitrate": 5000,
    "adaptiveStreaming": true
  }'
```

### 5.4 Delete Stream (SUPERADMIN/ADMIN/EDITOR/USER)
```bash
curl -X DELETE http://localhost:3000/streaming/api/stream/<STREAM_ID> \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Dashboard Service Endpoints

**Service Port**: 8084
**Route Prefix**: `/dashboard`

### 6.1 Get Dashboard Overview (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/dashboard/api/dashboard/overview \
  -H "Authorization: Bearer $TOKEN"
```

### 6.2 Get Analytics Data (SUPERADMIN/ADMIN Only)
```bash
curl -X GET http://localhost:3000/dashboard/api/dashboard/analytics \
  -H "Authorization: Bearer $TOKEN"
```

### 6.3 Create Dashboard Widget (SUPERADMIN/ADMIN Only)
```bash
curl -X POST http://localhost:3000/dashboard/api/dashboard/widgets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "chart",
    "title": "Video Views",
    "config": {}
  }'
```

### 6.4 Update Dashboard Settings (SUPERADMIN/ADMIN Only)
```bash
curl -X PUT http://localhost:3000/dashboard/api/dashboard/settings \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "theme": "dark",
    "refreshInterval": 30
  }'
```

### 6.5 Delete Dashboard Widget (SUPERADMIN/ADMIN Only)
```bash
curl -X DELETE http://localhost:3000/dashboard/api/dashboard/widgets/<WIDGET_ID> \
  -H "Authorization: Bearer $TOKEN"
```

---

## Troubleshooting

### Issue 1: 403 Forbidden Error After Login

**Symptoms:**
- Login succeeds and returns a valid token
- Using the token for upload/other endpoints returns 403 Forbidden

**Diagnostic Steps:**

#### Step 1: Verify Token is Valid
```bash
# Decode JWT token (using jwt.io or jwt-cli)
echo $TOKEN | cut -d. -f2 | base64 -d 2>/dev/null | jq .
```

**Expected payload:**
```json
{
  "uid": "550e8400-e29b-41d4-a716-446655440000",
  "email": "superadmin@apigateway.local",
  "role": "SUPERADMIN",
  "name": "Super Administrator",
  "iat": 1703001600,
  "exp": 1703088000
}
```

#### Step 2: Check Current User Role
```bash
curl -X GET http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "superadmin@apigateway.local",
  "role": "SUPERADMIN",
  "name": "Super Administrator",
  "isActive": true
}
```

#### Step 3: Check Application Logs

The enhanced logging will show:
```
[INFO] Authentication successful for user: superadmin@apigateway.local (SUPERADMIN)
[DEBUG] Authorization check - Path: /videos/api/media/upload, Method: POST, User: superadmin@apigateway.local (SUPERADMIN), Required roles: [SUPERADMIN, ADMIN, EDITOR]
[INFO] Authorization successful - Forwarding POST /videos/api/media/upload for user superadmin@apigateway.local (SUPERADMIN)
```

If you see:
```
[WARN] Authorization failed - Path: /videos/api/media/upload, Method: POST, User: superadmin@apigateway.local (SUPERADMIN), Required: SUPERADMIN or ADMIN or EDITOR
```

This indicates a bug in the authorization logic.

#### Step 4: Verify Database User Exists
Connect to PostgreSQL and check:
```sql
-- Connect to database
psql -U postgres -d api_gateway

-- Check if superadmin exists
SELECT id, email, role, is_active, name
FROM users
WHERE email = 'superadmin@apigateway.local';
```

**Expected Result:**
```
                  id                  |           email                  |    role     | is_active |        name
--------------------------------------+----------------------------------+-------------+-----------+---------------------
 550e8400-e29b-41d4-a716-446655440000 | superadmin@apigateway.local      | SUPERADMIN  | t         | Super Administrator
```

If user doesn't exist, restart the application to trigger initialization.

#### Step 5: Verify JWT Secret Configuration

Check that the JWT secret is consistent:
```bash
# In application.properties or environment variables
echo $JWT_SECRET
```

Default: `your-super-secret-jwt-key-for-testing-only`

#### Step 6: Check Token Expiration
JWT tokens expire after 24 hours (86400000ms). If your token is expired:
```bash
# Get a fresh token
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "superadmin@apigateway.local",
    "password": "SuperAdmin@123"
  }'
```

### Issue 2: 401 Unauthorized Error

**Possible Causes:**
1. No token provided
2. Token malformed (missing "Bearer " prefix)
3. Token expired
4. Invalid JWT signature (secret mismatch)
5. User doesn't exist in database
6. User is inactive

**Solutions:**
```bash
# Correct format
curl -H "Authorization: Bearer $TOKEN" ...

# Incorrect formats (will fail)
curl -H "Authorization: $TOKEN" ...        # Missing "Bearer"
curl -H "Token: $TOKEN" ...                # Wrong header name
```

### Issue 3: Connection Refused

**Check all services are running:**
```bash
# API Gateway (Port 3000)
curl http://localhost:3000/health

# Catalog Service (Port 8081)
curl http://localhost:8081/actuator/health

# Streaming Service (Port 8082)
curl http://localhost:8082/actuator/health

# Dashboard Service (Port 8084)
curl http://localhost:8084/actuator/health

# Video Ingestion Service (Port 8087)
curl http://localhost:8087/actuator/health

# User Service (Port 8089)
curl http://localhost:8089/actuator/health
```

### Issue 4: File Upload Fails

**Common Problems:**
1. File size exceeds 2GB limit
2. Wrong content type
3. File path incorrect
4. Storage directory doesn't exist

**Solution:**
```bash
# Check file exists
ls -lh /path/to/video.mp4

# Verify file size (must be < 2GB)
# Create storage directories if they don't exist
mkdir -p storage/raw/videos
mkdir -p storage/raw/posters

# Ensure correct permissions
chmod -R 755 storage
```

### Issue 5: Route Not Found (404)

**Common Mistakes:**
```bash
# WRONG - accessing backend service directly
curl http://localhost:8087/api/media/upload

# CORRECT - through API Gateway with route prefix
curl http://localhost:3000/videos/api/media/upload
```

**Route Prefixes:**
- `/catalog/**` → http://localhost:8081
- `/users/**` → http://localhost:8089
- `/stats/**` → http://localhost:8089
- `/videos/**` → http://localhost:8087
- `/streaming/**` → http://localhost:8082
- `/dashboard/**` → http://localhost:8084

---

## Testing Complete Workflow

### Scenario: Upload and Manage a Video

```bash
# 1. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "superadmin@apigateway.local",
    "password": "SuperAdmin@123"
  }')

# Extract token (requires jq)
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: $TOKEN"

# 2. Upload a poster
POSTER_RESPONSE=$(curl -s -X POST http://localhost:3000/videos/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/poster.jpg" \
  -F "type=POSTER")

POSTER_ID=$(echo $POSTER_RESPONSE | jq -r '.id')
echo "Poster ID: $POSTER_ID"

# 3. Upload a video
VIDEO_RESPONSE=$(curl -s -X POST http://localhost:3000/videos/api/media/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/video.mp4" \
  -F "type=VIDEO" \
  -F "language=en")

VIDEO_ID=$(echo $VIDEO_RESPONSE | jq -r '.id')
echo "Video ID: $VIDEO_ID"

# 4. Create catalog entry
CATALOG_RESPONSE=$(curl -s -X POST http://localhost:3000/catalog/api/catalog \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"My Awesome Video\",
    \"description\": \"A great video\",
    \"videoId\": \"$VIDEO_ID\",
    \"posterId\": \"$POSTER_ID\",
    \"genre\": \"Documentary\",
    \"releaseYear\": 2024,
    \"duration\": 120,
    \"rating\": 4.5
  }")

CATALOG_ID=$(echo $CATALOG_RESPONSE | jq -r '.id')
echo "Catalog ID: $CATALOG_ID"

# 5. Get catalog entry
curl -s -X GET http://localhost:3000/catalog/api/catalog/$CATALOG_ID | jq .

# 6. Start streaming session
curl -s -X POST http://localhost:3000/streaming/api/stream/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"videoId\": \"$VIDEO_ID\",
    \"quality\": \"1080p\"
  }" | jq .

echo "Workflow completed successfully!"
```

---

## Quick Reference Table

| Endpoint Pattern | Method | Access Level | Description |
|-----------------|--------|--------------|-------------|
| `/api/auth/login` | POST | Public | Get JWT token |
| `/api/auth/me` | GET | Authenticated | Get current user info |
| `/api/auth/register` | POST | SUPERADMIN | Create new user |
| `/api/auth/users` | GET | SUPERADMIN, ADMIN | List all users |
| `/catalog/**` | GET | Public | View catalog |
| `/catalog/**` | POST/PUT/PATCH | SUPERADMIN, ADMIN, EDITOR | Modify catalog |
| `/catalog/**` | DELETE | SUPERADMIN, ADMIN | Delete catalog items |
| `/users/**` | GET/POST/DELETE | SUPERADMIN, ADMIN | Manage users |
| `/users/**` | PUT/PATCH | SUPERADMIN, ADMIN, USER | Update user info |
| `/videos/api/media/upload` | POST | SUPERADMIN, ADMIN, EDITOR | **Upload videos/posters** |
| `/videos/**` | GET | SUPERADMIN, ADMIN, EDITOR, USER | View videos |
| `/videos/**` | PUT/PATCH | SUPERADMIN, ADMIN, EDITOR | Update videos |
| `/videos/**` | DELETE | SUPERADMIN, ADMIN | Delete videos |
| `/streaming/**` | GET | Public | View streams |
| `/streaming/**` | POST/DELETE | SUPERADMIN, ADMIN, EDITOR, USER | Manage streams |
| `/dashboard/**` | ALL | SUPERADMIN, ADMIN | Dashboard operations |

---

## Security Notes

1. **Change Default Password**: The default superadmin password should be changed immediately in production
2. **JWT Secret**: Use a strong, random JWT secret in production (set via `JWT_SECRET` environment variable)
3. **HTTPS**: Use HTTPS in production (configure SSL/TLS certificates)
4. **Rate Limiting**: Consider implementing rate limiting for login endpoint
5. **File Upload Limits**: Default 2GB limit - adjust in `application.properties` if needed
6. **CORS**: Current configuration allows all origins (`*`) - restrict in production

---

## Additional Resources

- **Health Check**: `GET http://localhost:3000/health`
- **Application Logs**: Check console output for detailed authentication/authorization logs
- **Database**: PostgreSQL at `localhost:5432/api_gateway`
- **JWT Decoder**: https://jwt.io for debugging tokens

---

**Generated**: 2025-12-30
**Version**: 1.0
**API Gateway Port**: 3000
