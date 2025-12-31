package com.example.playback.dto.catalog;

import lombok.Data;
import java.time.Instant;

@Data
public class MediaResponse {
    private Long id;
    private String language;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private String subtitleUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
