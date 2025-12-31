
package com.example.playback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PlaybackRequest {

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "User ID contains invalid characters")
    private String userId;

    @NotBlank(message = "Video ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Video ID contains invalid characters")
    private String videoId;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
}
