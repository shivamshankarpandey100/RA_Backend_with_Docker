
package com.example.playback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "watch_progress")
public class WatchProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String videoId;
    private Long watchedSeconds;
    private Long totalSeconds;
    private boolean completed;
    private LocalDateTime updatedAt;
}
