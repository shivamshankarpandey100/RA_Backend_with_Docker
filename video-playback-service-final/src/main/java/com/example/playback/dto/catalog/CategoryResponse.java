package com.example.playback.dto.catalog;

import lombok.Data;
import java.time.Instant;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
