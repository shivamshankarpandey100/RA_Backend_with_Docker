
package com.example.playback.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ProgressRequest {

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "User ID contains invalid characters")
    private String userId;

    @NotBlank(message = "Video ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Video ID contains invalid characters")
    private String videoId;

    @NotNull(message = "Watched seconds is required")
    @Min(value = 0, message = "Watched seconds must be non-negative")
    private Long watchedSeconds;

    @NotNull(message = "Total seconds is required")
    @Min(value = 1, message = "Total seconds must be positive")
    private Long totalSeconds;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public Long getWatchedSeconds() { return watchedSeconds; }
    public void setWatchedSeconds(Long watchedSeconds) { this.watchedSeconds = watchedSeconds; }

    public Long getTotalSeconds() { return totalSeconds; }
    public void setTotalSeconds(Long totalSeconds) { this.totalSeconds = totalSeconds; }
}
