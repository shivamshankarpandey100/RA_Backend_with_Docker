package com.example.playback.dto.catalog;

import lombok.Data;
import java.time.Instant;

@Data
public class CarouselResponse {
    private Long id;
    private Long contentId;
    private String title;
    private String subtitle;
    private String type;
    private Integer order;
    private Boolean active;
    private String contentTitle;
    private String thumbnailUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
