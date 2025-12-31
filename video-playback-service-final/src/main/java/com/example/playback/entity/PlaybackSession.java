
package com.example.playback.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "playback_sessions")
public class PlaybackSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sessionId;
    private String userId;
    private String videoId;
    private LocalDateTime startedAt;
    private LocalDateTime lastHeartbeat;
    private boolean active;
}
