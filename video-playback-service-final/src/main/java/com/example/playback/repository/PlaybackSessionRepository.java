
package com.example.playback.repository;

import com.example.playback.entity.PlaybackSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaybackSessionRepository extends JpaRepository<PlaybackSession, Long> {
    Optional<PlaybackSession> findBySessionId(String sessionId);
}
