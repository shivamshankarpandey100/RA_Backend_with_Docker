package com.example.playback.dto.catalog;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ContentResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String type;
    private Long categoryId;
    private String categoryName;
    private String thumbnailUrl;
    private String videoUrl;
    private Integer duration;
    private Boolean active;
    private List<MediaResponse> media;
    private Instant createdAt;
    private Instant updatedAt;
}
