# Video Playback Service - Catalog Integration Test Guide

## Overview
This guide covers testing the video-playback-service-final with its integration to the RA_CatalogService. The playback service now consumes catalog data to provide video streaming with metadata from the catalog.

## Base URL
```
http://localhost:8082
```

## Service Architecture
```
┌─────────────────────────────────────┐
│   Video Playback Service (8082)     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Playback Features          │   │
│  │  - Video Streaming          │   │
│  │  - Session Management       │   │
│  │  - Progress Tracking        │   │
│  └─────────────────────────────┘   │
│              ▼                      │
│  ┌─────────────────────────────┐   │
│  │  Catalog Integration        │   │
│  │  - Content Metadata         │   │
│  │  - Categories               │   │
│  │  - Featured Carousel        │   │
│  │  - Multi-language Support   │   │
│  └─────────────────────────────┘   │
│              │                      │
└──────────────┼──────────────────────┘
               │ HTTP REST Client
               ▼
┌─────────────────────────────────────┐
│   RA_CatalogService (8081)          │
│   - Content Management              │
│   - Category Management             │
│   - Carousel Management             │
└─────────────────────────────────────┘
```

---

## Prerequisites
1. All services running via Docker Compose:
   ```bash
   docker-compose up --build
   ```

2. Services should be healthy:
   - `postgres-playback` (Port 5437)
   - `postgres-catalog` (Port 5433)
   - `catalog-service` (Port 8081)
   - `playback-service` (Port 8082)

3. Verify services are running:
   ```bash
   docker-compose ps
   ```

---

## Part 1: Catalog Integration Endpoints

### 1.1 Get All Categories
**Endpoint:** `GET /api/catalog/categories`

**Description:** Fetches all available categories from the catalog service

```bash
curl -X GET http://localhost:8082/api/catalog/categories
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Action",
    "description": "Action-packed content",
    "active": true,
    "createdAt": "2025-12-30T10:00:00Z",
    "updatedAt": "2025-12-30T10:00:00Z"
  },
  {
    "id": 2,
    "name": "Drama",
    "description": "Dramatic series and films",
    "active": true,
    "createdAt": "2025-12-30T10:00:00Z",
    "updatedAt": "2025-12-30T10:00:00Z"
  }
]
```

---

### 1.2 Get Category by ID
**Endpoint:** `GET /api/catalog/categories/{id}`

**Description:** Fetch a specific category by its ID

```bash
curl -X GET http://localhost:8082/api/catalog/categories/1
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "name": "Action",
  "description": "Action-packed content",
  "active": true,
  "createdAt": "2025-12-30T10:00:00Z",
  "updatedAt": "2025-12-30T10:00:00Z"
}
```

---

### 1.3 Get Content by ID
**Endpoint:** `GET /api/catalog/content/{id}`

**Description:** Fetch complete video content metadata including all language variants

```bash
curl -X GET http://localhost:8082/api/catalog/content/1
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "title": "The Adventure Begins",
  "description": "An epic journey starts here",
  "status": "PUBLISHED",
  "type": "SERIES",
  "categoryId": 1,
  "categoryName": "Action",
  "thumbnailUrl": "https://cdn.example.com/thumbnails/adventure.jpg",
  "videoUrl": "https://cdn.example.com/videos/adventure-ep1.mp4",
  "duration": 2400,
  "active": true,
  "media": [
    {
      "id": 1,
      "language": "en",
      "title": "The Adventure Begins",
      "description": "An epic journey starts here",
      "thumbnailUrl": "https://cdn.example.com/thumbnails/adventure-en.jpg",
      "videoUrl": "https://cdn.example.com/videos/adventure-ep1-en.mp4",
      "subtitleUrl": "https://cdn.example.com/subtitles/adventure-ep1-en.srt",
      "createdAt": "2025-12-30T10:00:00Z",
      "updatedAt": "2025-12-30T10:00:00Z"
    },
    {
      "id": 2,
      "language": "es",
      "title": "La Aventura Comienza",
      "description": "Un viaje épico comienza aquí",
      "thumbnailUrl": "https://cdn.example.com/thumbnails/adventure-es.jpg",
      "videoUrl": "https://cdn.example.com/videos/adventure-ep1-es.mp4",
      "subtitleUrl": "https://cdn.example.com/subtitles/adventure-ep1-es.srt",
      "createdAt": "2025-12-30T10:00:00Z",
      "updatedAt": "2025-12-30T10:00:00Z"
    }
  ],
  "createdAt": "2025-12-30T10:00:00Z",
  "updatedAt": "2025-12-30T10:00:00Z"
}
```

---

### 1.4 Get Published Content (with Filters)
**Endpoint:** `GET /api/catalog/content`

**Description:** Browse published content with filtering, searching, and pagination

**Filter by Type (SERIES):**
```bash
curl -X GET "http://localhost:8082/api/catalog/content?type=SERIES&page=0&size=10"
```

**Filter by Type (REEL):**
```bash
curl -X GET "http://localhost:8082/api/catalog/content?type=REEL&page=0&size=10"
```

**Filter by Category:**
```bash
curl -X GET "http://localhost:8082/api/catalog/content?categoryId=1&page=0&size=10"
```

**Search by Title:**
```bash
curl -X GET "http://localhost:8082/api/catalog/content?search=adventure&page=0&size=10"
```

**Combined Filters:**
```bash
curl -X GET "http://localhost:8082/api/catalog/content?type=SERIES&categoryId=1&search=adventure&page=0&size=10&sortBy=title&sortDir=ASC"
```

**Expected Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "The Adventure Begins",
      "description": "An epic journey starts here",
      "status": "PUBLISHED",
      "type": "SERIES",
      "categoryId": 1,
      "categoryName": "Action",
      "thumbnailUrl": "https://cdn.example.com/thumbnails/adventure.jpg",
      "videoUrl": "https://cdn.example.com/videos/adventure-ep1.mp4",
      "duration": 2400,
      "active": true,
      "media": [...],
      "createdAt": "2025-12-30T10:00:00Z",
      "updatedAt": "2025-12-30T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 25,
  "totalPages": 3,
  "last": false
}
```

---

### 1.5 Get Featured Carousel Items
**Endpoint:** `GET /api/catalog/carousel`

**Description:** Fetch all featured content for homepage carousel display

```bash
curl -X GET http://localhost:8082/api/catalog/carousel
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "contentId": 5,
    "title": "Featured: The Adventure Begins",
    "subtitle": "Now streaming - Season 1",
    "type": "SERIES",
    "order": 1,
    "active": true,
    "contentTitle": "The Adventure Begins",
    "thumbnailUrl": "https://cdn.example.com/carousel/adventure-banner.jpg",
    "createdAt": "2025-12-30T10:00:00Z",
    "updatedAt": "2025-12-30T10:00:00Z"
  },
  {
    "id": 2,
    "contentId": 12,
    "title": "Trending Now",
    "subtitle": "Top rated this week",
    "type": "REEL",
    "order": 2,
    "active": true,
    "contentTitle": "Viral Moments",
    "thumbnailUrl": "https://cdn.example.com/carousel/trending-banner.jpg",
    "createdAt": "2025-12-30T10:00:00Z",
    "updatedAt": "2025-12-30T10:00:00Z"
  }
]
```

---

### 1.6 Get Carousel Item by ID
**Endpoint:** `GET /api/catalog/carousel/{id}`

**Description:** Fetch a specific carousel item

```bash
curl -X GET http://localhost:8082/api/catalog/carousel/1
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "contentId": 5,
  "title": "Featured: The Adventure Begins",
  "subtitle": "Now streaming - Season 1",
  "type": "SERIES",
  "order": 1,
  "active": true,
  "contentTitle": "The Adventure Begins",
  "thumbnailUrl": "https://cdn.example.com/carousel/adventure-banner.jpg",
  "createdAt": "2025-12-30T10:00:00Z",
  "updatedAt": "2025-12-30T10:00:00Z"
}
```

---

### 1.7 Get Carousel Overview/Statistics
**Endpoint:** `GET /api/catalog/carousel/overview`

**Description:** Get carousel statistics (total items, active items, series count, reel count)

```bash
curl -X GET http://localhost:8082/api/catalog/carousel/overview
```

**Expected Response (200 OK):**
```json
{
  "totalItems": 10,
  "activeItems": 8,
  "seriesCount": 5,
  "reelCount": 3
}
```

---

## Part 2: Video Playback Endpoints

### 2.1 Get All Videos (Local Database)
**Endpoint:** `GET /api/videos`

**Description:** Retrieves all videos stored in the playback service's local database

```bash
curl -X GET http://localhost:8082/api/videos
```

**Expected Response (200 OK):**
```json
[
  {
    "id": "video1",
    "title": "Sample Video",
    "filePath": "videos/video1.mp4",
    "mimeType": "video/mp4",
    "duration": 120,
    "quality": "1080p"
  }
]
```

---

### 2.2 Start Playback Session
**Endpoint:** `POST /api/playback/start`

**Description:** Initiates a new playback session for a user and video

```bash
curl -X POST http://localhost:8082/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1"
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user123",
  "videoId": "video1",
  "startedAt": "2025-12-31T10:30:00",
  "lastHeartbeat": "2025-12-31T10:30:00",
  "active": true
}
```

**Save the `sessionId` for subsequent requests!**

---

### 2.3 Send Heartbeat
**Endpoint:** `POST /api/playback/heartbeat/{sessionId}`

**Description:** Updates the last heartbeat timestamp to keep session alive

```bash
# Replace with actual sessionId from step 2.2
curl -X POST http://localhost:8082/api/playback/heartbeat/550e8400-e29b-41d4-a716-446655440000
```

**Expected Response (200 OK):**
```
(Empty response body)
```

---

### 2.4 Update Watch Progress
**Endpoint:** `POST /api/progress/update`

**Description:** Updates or creates watch progress for a user's video

```bash
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": 45,
    "totalSeconds": 120
  }'
```

**Expected Response (200 OK):**
```
(Empty response body)
```

---

### 2.5 Stream Video (Full Content)
**Endpoint:** `GET /api/stream/{videoId}`

**Description:** Streams the entire video file with range support

```bash
curl -X GET http://localhost:8082/api/stream/video1 \
  --output downloaded_video.mp4
```

**Expected Response (206 Partial Content):**
- Video file is downloaded to `downloaded_video.mp4`
- Headers include:
  - `Content-Type: video/mp4`
  - `Accept-Ranges: bytes`
  - `Content-Range: bytes 0-{filesize-1}/{filesize}`

---

### 2.6 Stream Video (Range Request)
**Endpoint:** `GET /api/stream/{videoId}`

**Description:** Streams a specific byte range of the video (for seeking/resuming)

```bash
# Request first 1MB
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=0-1048575" \
  --output video_chunk.mp4
```

```bash
# Request from byte 1MB to end
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=1048576-" \
  --output video_remainder.mp4
```

**Expected Response (206 Partial Content):**
- Partial video content
- `Content-Range: bytes 0-1048575/{total_size}`

---

### 2.7 Stop Playback Session
**Endpoint:** `POST /api/playback/stop/{sessionId}`

**Description:** Marks a playback session as inactive

```bash
# Replace with actual sessionId
curl -X POST http://localhost:8082/api/playback/stop/550e8400-e29b-41d4-a716-446655440000
```

**Expected Response (200 OK):**
```
(Empty response body)
```

---

## Part 3: Complete Integration Test Flow

This demonstrates a complete user journey from browsing catalog to watching a video:

```bash
#!/bin/bash

echo "=== Step 1: Get Featured Carousel ==="
curl -X GET http://localhost:8082/api/catalog/carousel

echo -e "\n\n=== Step 2: Browse Categories ==="
curl -X GET http://localhost:8082/api/catalog/categories

echo -e "\n\n=== Step 3: Get SERIES Content from Category 1 ==="
curl -X GET "http://localhost:8082/api/catalog/content?type=SERIES&categoryId=1&page=0&size=5"

echo -e "\n\n=== Step 4: Get Detailed Content Metadata ==="
curl -X GET http://localhost:8082/api/catalog/content/1

echo -e "\n\n=== Step 5: Start Playback Session ==="
SESSION_RESPONSE=$(curl -s -X POST http://localhost:8082/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "videoId": "video1"}')

echo $SESSION_RESPONSE

# Extract sessionId (requires jq)
SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.sessionId')
echo "Session ID: $SESSION_ID"

echo -e "\n\n=== Step 6: Stream Video (First 10MB) ==="
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=0-10485759" \
  --output video_part1.mp4

echo -e "\n\n=== Step 7: Update Watch Progress (30 seconds watched) ==="
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": 30,
    "totalSeconds": 120
  }'

echo -e "\n\n=== Step 8: Send Heartbeat ==="
curl -X POST http://localhost:8082/api/playback/heartbeat/$SESSION_ID

echo -e "\n\n=== Step 9: Continue Streaming (Next 10MB) ==="
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=10485760-20971519" \
  --output video_part2.mp4

echo -e "\n\n=== Step 10: Update Progress (60 seconds watched) ==="
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": 60,
    "totalSeconds": 120
  }'

echo -e "\n\n=== Step 11: Stop Playback Session ==="
curl -X POST http://localhost:8082/api/playback/stop/$SESSION_ID

echo -e "\n\n=== Integration Test Complete ==="
```

---

## Part 4: Windows PowerShell Test Script

For Windows users, here's a PowerShell equivalent:

```powershell
# Complete Integration Test in PowerShell

Write-Host "=== Step 1: Get Featured Carousel ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/catalog/carousel" -Method Get | ConvertTo-Json -Depth 5

Write-Host "`n=== Step 2: Browse Categories ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/catalog/categories" -Method Get | ConvertTo-Json -Depth 5

Write-Host "`n=== Step 3: Get SERIES Content ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/catalog/content?type=SERIES&page=0&size=5" -Method Get | ConvertTo-Json -Depth 5

Write-Host "`n=== Step 4: Get Content by ID ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/catalog/content/1" -Method Get | ConvertTo-Json -Depth 5

Write-Host "`n=== Step 5: Start Playback Session ===" -ForegroundColor Green
$playbackBody = @{
    userId = "user123"
    videoId = "video1"
} | ConvertTo-Json

$session = Invoke-RestMethod -Uri "http://localhost:8082/api/playback/start" `
    -Method Post `
    -ContentType "application/json" `
    -Body $playbackBody

$sessionId = $session.sessionId
Write-Host "Session ID: $sessionId" -ForegroundColor Yellow
$session | ConvertTo-Json

Write-Host "`n=== Step 6: Update Progress ===" -ForegroundColor Green
$progressBody = @{
    userId = "user123"
    videoId = "video1"
    watchedSeconds = 45
    totalSeconds = 120
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/progress/update" `
    -Method Post `
    -ContentType "application/json" `
    -Body $progressBody

Write-Host "`n=== Step 7: Send Heartbeat ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/playback/heartbeat/$sessionId" -Method Post

Write-Host "`n=== Step 8: Stop Session ===" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8082/api/playback/stop/$sessionId" -Method Post

Write-Host "`n=== Integration Test Complete ===" -ForegroundColor Green
```

---

## Part 5: Health Check Endpoints

### 5.1 Playback Service Health
```bash
curl -X GET http://localhost:8082/actuator/health
```

### 5.2 Catalog Service Health (via Playback Service)
```bash
# Test if playback service can reach catalog service
curl -X GET http://localhost:8082/api/catalog/categories
```

If this fails, catalog service might not be reachable. Check:
```bash
docker-compose logs catalog-service
docker-compose logs playback-service
```

---

## Part 6: Error Scenarios

### 6.1 Content Not Found
```bash
curl -X GET http://localhost:8082/api/catalog/content/99999
```

**Expected Response (404 Not Found)**

---

### 6.2 Invalid Category ID
```bash
curl -X GET http://localhost:8082/api/catalog/categories/99999
```

**Expected Response (404 Not Found)**

---

### 6.3 Catalog Service Unavailable
If catalog service is down, you'll get:

```json
{
  "status": 500,
  "message": "Failed to fetch data from catalog service",
  "timestamp": "2025-12-31T10:30:00"
}
```

**Troubleshooting:**
```bash
# Check if catalog service is running
docker-compose ps catalog-service

# View catalog service logs
docker-compose logs catalog-service

# Restart catalog service
docker-compose restart catalog-service
```

---

## Part 7: Environment Variables

To customize the playback service configuration:

**Linux/macOS:**
```bash
export PLAYBACK_PORT=8082
export PLAYBACK_DB_NAME=video_streaming
export CATALOG_SERVICE_URL=http://catalog-service:8081
export VIDEO_STORAGE_PATH=/app/videos
```

**Windows:**
```cmd
set PLAYBACK_PORT=8082
set PLAYBACK_DB_NAME=video_streaming
set CATALOG_SERVICE_URL=http://catalog-service:8081
set VIDEO_STORAGE_PATH=C:\app\videos
```

Then rebuild:
```bash
docker-compose up --build playback-service
```

---

## Part 8: Summary of All Endpoints

### Catalog Integration Endpoints (New)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/catalog/categories` | Get all categories |
| GET | `/api/catalog/categories/{id}` | Get category by ID |
| GET | `/api/catalog/content/{id}` | Get content by ID with metadata |
| GET | `/api/catalog/content` | Browse published content (filters) |
| GET | `/api/catalog/carousel` | Get featured carousel items |
| GET | `/api/catalog/carousel/{id}` | Get carousel item by ID |
| GET | `/api/catalog/carousel/overview` | Get carousel statistics |

### Playback Service Endpoints (Existing)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/videos` | Get all local videos |
| POST | `/api/playback/start` | Start playback session |
| POST | `/api/playback/heartbeat/{sessionId}` | Send session heartbeat |
| POST | `/api/playback/stop/{sessionId}` | Stop playback session |
| POST | `/api/progress/update` | Update watch progress |
| GET | `/api/stream/{videoId}` | Stream video (with range support) |

### Health Check
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Service health status |

---

## Part 9: Quick Reference Commands

### Start the entire stack:
```bash
docker-compose up --build
```

### View service logs:
```bash
docker-compose logs -f playback-service
docker-compose logs -f catalog-service
```

### Rebuild just playback service:
```bash
docker-compose up --build playback-service
```

### Stop all services:
```bash
docker-compose down
```

### Remove all data (volumes):
```bash
docker-compose down -v
```

### Access database directly:
```bash
# Playback database
docker exec -it postgres-playback psql -U postgres -d video_streaming

# Catalog database
docker exec -it postgres-catalog psql -U postgres -d newcatalog
```

---

## Troubleshooting

### Issue: "Connection refused" when calling catalog endpoints
**Solution:**
1. Verify catalog service is running: `docker-compose ps catalog-service`
2. Check network connectivity: `docker exec playback-service ping catalog-service`
3. Review logs: `docker-compose logs catalog-service`

### Issue: Playback service won't start
**Solution:**
1. Check database health: `docker-compose ps postgres-playback`
2. View startup logs: `docker-compose logs playback-service`
3. Verify Dockerfile build: `docker-compose build playback-service`

### Issue: Videos not streaming
**Solution:**
1. Ensure video files exist in `/app/videos` volume
2. Check file permissions
3. Verify video entity in database

---

## Notes
- All catalog data is fetched in real-time from RA_CatalogService
- Playback sessions are stored locally in the playback service database
- Video files are stored in Docker volume `playback-video-storage`
- Multi-language support is available through the catalog service's media endpoints
- Range requests are supported for efficient video streaming and seeking
