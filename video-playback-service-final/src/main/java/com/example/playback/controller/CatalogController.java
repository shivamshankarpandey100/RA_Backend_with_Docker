package com.example.playback.controller;

import com.example.playback.client.CatalogServiceClient;
import com.example.playback.dto.catalog.CarouselResponse;
import com.example.playback.dto.catalog.CategoryResponse;
import com.example.playback.dto.catalog.ContentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogServiceClient catalogServiceClient;

    public CatalogController(CatalogServiceClient catalogServiceClient) {
        this.catalogServiceClient = catalogServiceClient;
    }

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(catalogServiceClient.getAllCategories());
    }

    /**
     * Get category by ID
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogServiceClient.getCategoryById(id));
    }

    /**
     * Get content by ID with all metadata and media variants
     */
    @GetMapping("/content/{id}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogServiceClient.getContentById(id));
    }

    /**
     * Get published content with filtering and pagination
     */
    @GetMapping("/content")
    public ResponseEntity<Map<String, Object>> getPublishedContent(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        return ResponseEntity.ok(catalogServiceClient.getPublishedContent(
                type, categoryId, search, page, size, sortBy, sortDir));
    }

    /**
     * Get all carousel items (featured content)
     */
    @GetMapping("/carousel")
    public ResponseEntity<List<CarouselResponse>> getAllCarouselItems() {
        return ResponseEntity.ok(catalogServiceClient.getAllCarouselItems());
    }

    /**
     * Get carousel item by ID
     */
    @GetMapping("/carousel/{id}")
    public ResponseEntity<CarouselResponse> getCarouselById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogServiceClient.getCarouselById(id));
    }

    /**
     * Get carousel overview/statistics
     */
    @GetMapping("/carousel/overview")
    public ResponseEntity<Map<String, Object>> getCarouselOverview() {
        return ResponseEntity.ok(catalogServiceClient.getCarouselOverview());
    }
}
