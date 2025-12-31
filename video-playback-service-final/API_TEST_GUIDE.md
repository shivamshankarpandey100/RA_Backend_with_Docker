# Video Playback Service - API Testing Guide

## Base URL
```
http://localhost:8082
```

## Prerequisites
Before testing, ensure:
1. PostgreSQL is running on localhost:5432
2. Database `video_streaming` exists
3. Application is running on port 8082
4. At least one video file exists in the `videos/` directory (e.g., `videos/video1.mp4`)

---

## 1. Get All Videos
**Endpoint:** `GET /api/videos`

**Description:** Retrieves all available videos from the database

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

## 2. Start Playback Session
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
  "startedAt": "2025-12-30T10:30:00",
  "lastHeartbeat": "2025-12-30T10:30:00",
  "active": true
}
```

**Save the `sessionId` for subsequent requests!**

---

## 3. Send Heartbeat
**Endpoint:** `POST /api/playback/heartbeat/{sessionId}`

**Description:** Updates the last heartbeat timestamp to keep session alive

```bash
# Replace {sessionId} with the actual session ID from step 2
curl -X POST http://localhost:8082/api/playback/heartbeat/550e8400-e29b-41d4-a716-446655440000
```

**Expected Response (200 OK):**
```
(Empty response body)
```

---

## 4. Update Watch Progress
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

**Notes:**
- If progress already exists for this user/video, it will be updated
- `completed` flag is automatically set to true when watchedSeconds >= totalSeconds

---

## 5. Stream Video (Full Content)
**Endpoint:** `GET /api/stream/{videoId}`

**Description:** Streams the entire video file

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

## 6. Stream Video (Range Request)
**Endpoint:** `GET /api/stream/{videoId}`

**Description:** Streams a specific byte range of the video (for seeking/resuming)

```bash
# Request bytes 0-1023 (first 1KB)
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=0-1023" \
  --output video_chunk.mp4
```

```bash
# Request from byte 1024 to end
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=1024-" \
  --output video_chunk.mp4
```

**Expected Response (206 Partial Content):**
- Partial video content
- `Content-Range: bytes 0-1023/{total_size}`

---

## 7. Stop Playback Session
**Endpoint:** `POST /api/playback/stop/{sessionId}`

**Description:** Marks a playback session as inactive

```bash
# Replace {sessionId} with the actual session ID
curl -X POST http://localhost:8082/api/playback/stop/550e8400-e29b-41d4-a716-446655440000
```

**Expected Response (200 OK):**
```
(Empty response body)
```

---

## Error Scenarios

### 1. Invalid Video ID (Path Traversal Attempt)
```bash
curl -X POST http://localhost:8082/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "../../../etc/passwd"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "videoId": "Video ID contains invalid characters"
  },
  "timestamp": "2025-12-30T10:30:00"
}
```

### 2. Missing Required Fields
```bash
curl -X POST http://localhost:8082/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{
    "userId": ""
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "userId": "User ID is required",
    "videoId": "Video ID is required"
  },
  "timestamp": "2025-12-30T10:30:00"
}
```

### 3. Session Not Found
```bash
curl -X POST http://localhost:8082/api/playback/heartbeat/invalid-session-id
```

**Expected Response (404 Not Found):**
```json
{
  "status": 404,
  "message": "Playback session not found: invalid-session-id",
  "timestamp": "2025-12-30T10:30:00"
}
```

### 4. Video File Not Found
```bash
curl -X GET http://localhost:8082/api/stream/nonexistent-video
```

**Expected Response (404 Not Found):**
```json
{
  "status": 404,
  "message": "Video file not found: nonexistent-video",
  "timestamp": "2025-12-30T10:30:00"
}
```

### 5. Negative Watch Progress
```bash
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": -10,
    "totalSeconds": 120
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "watchedSeconds": "Watched seconds must be non-negative"
  },
  "timestamp": "2025-12-30T10:30:00"
}
```

---

## Complete Test Flow

Here's a complete test scenario:

```bash
# 1. Get all available videos
curl -X GET http://localhost:8082/api/videos

# 2. Start a playback session (save the sessionId from response)
SESSION_ID=$(curl -s -X POST http://localhost:8082/api/playback/start \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "videoId": "video1"}' | grep -o '"sessionId":"[^"]*"' | cut -d'"' -f4)

echo "Session ID: $SESSION_ID"

# 3. Stream video with range request (simulating video player)
curl -X GET http://localhost:8082/api/stream/video1 \
  -H "Range: bytes=0-1048575" \
  --output video_part1.mp4

# 4. Update watch progress (user watched 30 seconds)
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": 30,
    "totalSeconds": 120
  }'

# 5. Send heartbeat
curl -X POST http://localhost:8082/api/playback/heartbeat/$SESSION_ID

# 6. Update progress again (user watched 60 seconds)
curl -X POST http://localhost:8082/api/progress/update \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "videoId": "video1",
    "watchedSeconds": 60,
    "totalSeconds": 120
  }'

# 7. Stop playback session
curl -X POST http://localhost:8082/api/playback/stop/$SESSION_ID

echo "Test flow completed!"
```

---

## Windows PowerShell Alternative

For Windows users using PowerShell:

```powershell
# Get all videos
Invoke-RestMethod -Uri "http://localhost:8082/api/videos" -Method Get

# Start playback session
$body = @{
    userId = "user123"
    videoId = "video1"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8082/api/playback/start" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body

$sessionId = $response.sessionId
Write-Host "Session ID: $sessionId"

# Send heartbeat
Invoke-RestMethod -Uri "http://localhost:8082/api/playback/heartbeat/$sessionId" -Method Post

# Update progress
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

# Stop session
Invoke-RestMethod -Uri "http://localhost:8082/api/playback/stop/$sessionId" -Method Post
```

---

## Environment Variables

To use custom database credentials, set these environment variables before starting the application:

**Linux/macOS:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/video_streaming
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export VIDEO_STORAGE_PATH=/path/to/videos
```

**Windows:**
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/video_streaming
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
set VIDEO_STORAGE_PATH=C:\path\to\videos
```

---

## Summary of Fixed Issues

The following critical issues have been resolved:

1. **Path Traversal Vulnerability** - VideoId is now validated with regex pattern
2. **Resource Leak** - FileInputStream now uses try-with-resources
3. **Hardcoded Credentials** - Database credentials now use environment variables
4. **Input Validation** - DTOs have validation annotations (@NotBlank, @Pattern, @Min)
5. **Exception Handling** - Custom exceptions with proper HTTP status codes
6. **Query Methods** - Added findByUserIdAndVideoId() for efficient queries
7. **Progress Tracking** - Now updates existing progress instead of creating duplicates
