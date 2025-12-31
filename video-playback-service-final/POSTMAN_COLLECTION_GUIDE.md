# Video Playback Service - Postman Collection Guide

## 📦 Collection File
**File:** `Video_Playback_Service_Postman_Collection.json`

---

## 📥 How to Import into Postman

### Method 1: Direct Import
1. Open Postman
2. Click **Import** button (top left)
3. Click **Upload Files**
4. Select `Video_Playback_Service_Postman_Collection.json`
5. Click **Import**

### Method 2: Drag & Drop
1. Open Postman
2. Drag the JSON file into Postman window
3. Click **Import**

---

## 📂 Collection Structure

```
Video Playback Service - Complete API Collection
├── 📁 Catalog Integration (14 requests)
│   ├── 📁 Categories (2 requests)
│   │   ├── Get All Categories
│   │   └── Get Category by ID
│   ├── 📁 Content (7 requests)
│   │   ├── Get Content by ID
│   │   ├── Get All Published Content
│   │   ├── Filter Content by Type - SERIES
│   │   ├── Filter Content by Type - REEL
│   │   ├── Filter Content by Category
│   │   ├── Search Content by Title
│   │   └── Combined Filters - SERIES + Category + Search
│   └── 📁 Carousel (Featured Content) (3 requests)
│       ├── Get All Carousel Items
│       ├── Get Carousel Item by ID
│       └── Get Carousel Overview/Statistics
├── 📁 Video Playback (4 requests)
│   ├── Get All Videos
│   ├── Stream Video - Full Content
│   ├── Stream Video - Range Request (First 1MB)
│   └── Stream Video - Range Request (Custom)
├── 📁 Playback Sessions (3 requests)
│   ├── Start Playback Session (auto-saves sessionId)
│   ├── Send Heartbeat
│   └── Stop Playback Session
├── 📁 Watch Progress (4 requests)
│   ├── Update Watch Progress
│   ├── Update Progress - 25% Watched
│   ├── Update Progress - 50% Watched
│   └── Update Progress - 100% Watched (Completed)
├── 📁 Health & Status (2 requests)
│   ├── Service Health Check
│   └── Service Info
└── 📁 Complete User Journey (10 requests)
    ├── 1. Browse Featured Content (Carousel)
    ├── 2. Browse Categories
    ├── 3. Browse Content by Category
    ├── 4. Get Content Details
    ├── 5. Start Playback Session
    ├── 6. Stream Video (First Chunk)
    ├── 7. Update Progress (30 seconds)
    ├── 8. Send Heartbeat
    ├── 9. Update Progress (60 seconds)
    └── 10. Stop Playback Session
```

**Total Requests:** 37

---

## ⚙️ Collection Variables

The collection includes these pre-configured variables:

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8082` | Base URL for playback service |
| `sessionId` | (auto-populated) | Playback session ID (saved automatically) |

### How to Change Variables:
1. Click on the collection name
2. Go to **Variables** tab
3. Update the **Current Value** column
4. Click **Save**

---

## 🎯 Quick Start Guide

### 1. Health Check
Run this first to verify the service is running:
```
Health & Status > Service Health Check
```

### 2. Browse Catalog
Test catalog integration:
```
Catalog Integration > Categories > Get All Categories
Catalog Integration > Carousel > Get All Carousel Items
```

### 3. Start Playback
Test video playback:
```
Playback Sessions > Start Playback Session
```
This will automatically save the `sessionId` for subsequent requests.

### 4. Complete Flow
Run all requests in the **Complete User Journey** folder in order (1-10) to simulate a real user experience.

---

## 🔑 Key Features

### ✅ Auto-Save Session ID
When you run **"Start Playback Session"**, the `sessionId` from the response is automatically saved to the collection variable. All subsequent requests (heartbeat, stop session) will use this saved ID.

**Script in "Start Playback Session":**
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.collectionVariables.set("sessionId", jsonData.sessionId);
    console.log("Session ID saved: " + jsonData.sessionId);
}
```

### ✅ Range Request Examples
Video streaming endpoints include pre-configured range headers for testing:
- **First 1MB:** `Range: bytes=0-1048575`
- **First 10MB:** `Range: bytes=0-10485759`
- **From 1MB to end:** `Range: bytes=1048576-`

### ✅ Pre-filled Request Bodies
All POST requests have example request bodies with realistic data:
```json
{
  "userId": "user123",
  "videoId": "video1",
  "watchedSeconds": 45,
  "totalSeconds": 120
}
```

---

## 📋 All Endpoints Reference

### **Catalog Integration**

#### Categories
| Name | Method | Endpoint |
|------|--------|----------|
| Get All Categories | GET | `/api/catalog/categories` |
| Get Category by ID | GET | `/api/catalog/categories/{id}` |

#### Content
| Name | Method | Endpoint |
|------|--------|----------|
| Get Content by ID | GET | `/api/catalog/content/{id}` |
| Get All Published Content | GET | `/api/catalog/content?page=0&size=20` |
| Filter by Type (SERIES) | GET | `/api/catalog/content?type=SERIES` |
| Filter by Type (REEL) | GET | `/api/catalog/content?type=REEL` |
| Filter by Category | GET | `/api/catalog/content?categoryId=1` |
| Search by Title | GET | `/api/catalog/content?search=adventure` |
| Combined Filters | GET | `/api/catalog/content?type=SERIES&categoryId=1&search=action` |

#### Carousel
| Name | Method | Endpoint |
|------|--------|----------|
| Get All Carousel Items | GET | `/api/catalog/carousel` |
| Get Carousel by ID | GET | `/api/catalog/carousel/{id}` |
| Get Carousel Overview | GET | `/api/catalog/carousel/overview` |

---

### **Video Playback**

| Name | Method | Endpoint | Headers |
|------|--------|----------|---------|
| Get All Videos | GET | `/api/videos` | - |
| Stream Full Video | GET | `/api/stream/{videoId}` | - |
| Stream Range (1MB) | GET | `/api/stream/{videoId}` | `Range: bytes=0-1048575` |
| Stream Range (Custom) | GET | `/api/stream/{videoId}` | `Range: bytes=1048576-` |

---

### **Playback Sessions**

| Name | Method | Endpoint | Body |
|------|--------|----------|------|
| Start Session | POST | `/api/playback/start` | `{"userId":"user123","videoId":"video1"}` |
| Send Heartbeat | POST | `/api/playback/heartbeat/{sessionId}` | - |
| Stop Session | POST | `/api/playback/stop/{sessionId}` | - |

---

### **Watch Progress**

| Name | Method | Endpoint | Body |
|------|--------|----------|------|
| Update Progress | POST | `/api/progress/update` | `{"userId":"user123","videoId":"video1","watchedSeconds":45,"totalSeconds":120}` |
| 25% Progress | POST | `/api/progress/update` | `{"userId":"user123","videoId":"video1","watchedSeconds":30,"totalSeconds":120}` |
| 50% Progress | POST | `/api/progress/update` | `{"userId":"user123","videoId":"video1","watchedSeconds":60,"totalSeconds":120}` |
| 100% Progress | POST | `/api/progress/update` | `{"userId":"user123","videoId":"video1","watchedSeconds":120,"totalSeconds":120}` |

---

### **Health & Status**

| Name | Method | Endpoint |
|------|--------|----------|
| Health Check | GET | `/actuator/health` |
| Service Info | GET | `/actuator/info` |

---

## 🧪 Testing Scenarios

### Scenario 1: Basic Catalog Browse
1. Get All Categories
2. Get All Carousel Items
3. Get All Published Content

### Scenario 2: Content Discovery
1. Get All Categories
2. Filter Content by Category
3. Search Content by Title
4. Get Content by ID

### Scenario 3: Video Playback Flow
1. Start Playback Session
2. Stream Video (Range Request)
3. Update Watch Progress
4. Send Heartbeat
5. Stop Playback Session

### Scenario 4: Complete User Journey
Run all 10 requests in the **"Complete User Journey"** folder in sequential order.

---

## 🔧 Customization Tips

### Change Base URL for Different Environments:
1. Click collection name
2. Go to **Variables** tab
3. Update `baseUrl`:
   - Local: `http://localhost:8082`
   - Docker: `http://localhost:8082`
   - Production: `https://api.yourapp.com`

### Add Authentication (if needed):
1. Click collection name
2. Go to **Authorization** tab
3. Select type (Bearer Token, API Key, etc.)
4. Add credentials

### Create Environment-Specific Variables:
1. Click **Environments** (left sidebar)
2. Create **Development**, **Staging**, **Production** environments
3. Override `baseUrl` per environment

---

## 📊 Expected Responses

### Successful Responses

#### Get All Categories
```json
[
  {
    "id": 1,
    "name": "Action",
    "description": "Action-packed content",
    "active": true,
    "createdAt": "2025-12-30T10:00:00Z",
    "updatedAt": "2025-12-30T10:00:00Z"
  }
]
```

#### Get Content by ID
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
      "videoUrl": "https://cdn.example.com/videos/adventure-ep1-en.mp4",
      "subtitleUrl": "https://cdn.example.com/subtitles/adventure-ep1-en.srt"
    }
  ]
}
```

#### Start Playback Session
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

#### Health Check
```json
{
  "status": "UP"
}
```

---

## 🐛 Troubleshooting

### Issue: "Could not send request"
**Solution:** Verify the service is running:
```bash
docker-compose ps playback-service
```

### Issue: "404 Not Found" on catalog endpoints
**Solution:** Ensure catalog-service is running and healthy:
```bash
docker-compose ps catalog-service
curl http://localhost:8081/actuator/health
```

### Issue: sessionId not auto-saving
**Solution:**
1. Check Postman console (View > Show Postman Console)
2. Verify response status is 200
3. Check that Tests script is enabled in request

### Issue: Range requests not working
**Solution:**
1. Ensure video file exists in `/app/videos` volume
2. Check that `Range` header is properly set
3. Try without Range header first (full download)

---

## 🚀 Pro Tips

### 1. Use Collection Runner
Run all requests automatically:
1. Right-click collection
2. Select **Run Collection**
3. Select requests to run
4. Click **Run**

### 2. Export Results
After running requests:
1. Click **Export Results**
2. Save as JSON or HTML report

### 3. Share Collection
Export and share with team:
1. Right-click collection
2. Select **Export**
3. Choose Collection v2.1 format
4. Share JSON file

### 4. Create Tests
Add automated tests to verify responses:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has sessionId", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('sessionId');
});
```

---

## 📚 Additional Resources

- **Main Test Guide:** `CATALOG_INTEGRATION_TEST_GUIDE.md`
- **Original API Guide:** `API_TEST_GUIDE.md`
- **Architecture Documentation:** `../ARCHITECTURE.md`

---

## 📞 Support

If you encounter issues:
1. Check service health: `/actuator/health`
2. View service logs: `docker-compose logs playback-service`
3. Verify all services are running: `docker-compose ps`
4. Check network connectivity between services

---

**Collection Version:** 1.0
**Last Updated:** 2025-12-31
**Total Endpoints:** 37
**Service Port:** 8082
