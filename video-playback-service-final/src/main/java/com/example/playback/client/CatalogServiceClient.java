package com.example.playback.client;

import com.example.playback.dto.catalog.CarouselResponse;
import com.example.playback.dto.catalog.CategoryResponse;
import com.example.playback.dto.catalog.ContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class CatalogServiceClient {

    private final RestClient restClient;

    public CatalogServiceClient(
            @Value("${catalog.service.url:http://catalog-service:8081}") String catalogServiceUrl,
            RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(catalogServiceUrl)
                .build();
    }

    /**
     * Get all categories from catalog service
     */
    public List<CategoryResponse> getAllCategories() {
        return restClient.get()
                .uri("/api/v1/categories")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CategoryResponse>>() {});
    }

    /**
     * Get category by ID
     */
    public CategoryResponse getCategoryById(Long id) {
        return restClient.get()
                .uri("/api/v1/categories/{id}", id)
                .retrieve()
                .body(CategoryResponse.class);
    }

    /**
     * Get content by ID
     */
    public ContentResponse getContentById(Long id) {
        return restClient.get()
                .uri("/api/v1/content/{id}", id)
                .retrieve()
                .body(ContentResponse.class);
    }

    /**
     * Get all published content with optional filters
     */
    public Map<String, Object> getPublishedContent(String type, Long categoryId, String search,
                                                    int page, int size, String sortBy, String sortDir) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/content")
                        .queryParamIfPresent("type", java.util.Optional.ofNullable(type))
                        .queryParamIfPresent("categoryId", java.util.Optional.ofNullable(categoryId))
                        .queryParamIfPresent("search", java.util.Optional.ofNullable(search))
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", sortBy)
                        .queryParam("sortDir", sortDir)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Get all carousel items (featured content)
     */
    public List<CarouselResponse> getAllCarouselItems() {
        return restClient.get()
                .uri("/api/v1/carousel")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CarouselResponse>>() {});
    }

    /**
     * Get carousel item by ID
     */
    public CarouselResponse getCarouselById(Long id) {
        return restClient.get()
                .uri("/api/v1/carousel/{id}", id)
                .retrieve()
                .body(CarouselResponse.class);
    }

    /**
     * Get carousel overview/statistics
     */
    public Map<String, Object> getCarouselOverview() {
        return restClient.get()
                .uri("/api/v1/carousel/overview")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
