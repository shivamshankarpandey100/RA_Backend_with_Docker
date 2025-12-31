
package com.example.playback.service;

import com.example.playback.entity.PlaybackSession;
import com.example.playback.exception.ResourceNotFoundException;
import com.example.playback.repository.PlaybackSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PlaybackService {

    private final PlaybackSessionRepository repository;

    public PlaybackService(PlaybackSessionRepository repository) {
        this.repository = repository;
    }

    public PlaybackSession startSession(String userId, String videoId) {
        PlaybackSession session = new PlaybackSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setVideoId(videoId);
        session.setStartedAt(LocalDateTime.now());
        session.setLastHeartbeat(LocalDateTime.now());
        session.setActive(true);
        return repository.save(session);
    }

    public void heartbeat(String sessionId) {
        PlaybackSession session = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Playback session not found: " + sessionId));
        session.setLastHeartbeat(LocalDateTime.now());
        repository.save(session);
    }

    public void stop(String sessionId) {
        PlaybackSession session = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Playback session not found: " + sessionId));
        session.setActive(false);
        repository.save(session);
    }
}
