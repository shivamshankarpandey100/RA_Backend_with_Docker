
package com.example.playback.service;

import com.example.playback.dto.ProgressRequest;
import com.example.playback.entity.WatchProgress;
import com.example.playback.repository.WatchProgressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgressService {

    private final WatchProgressRepository repository;

    public ProgressService(WatchProgressRepository repository) {
        this.repository = repository;
    }

    public void updateProgress(ProgressRequest request) {
        WatchProgress progress = repository
                .findByUserIdAndVideoId(request.getUserId(), request.getVideoId())
                .orElse(new WatchProgress());

        progress.setUserId(request.getUserId());
        progress.setVideoId(request.getVideoId());
        progress.setWatchedSeconds(request.getWatchedSeconds());
        progress.setTotalSeconds(request.getTotalSeconds());
        progress.setCompleted(request.getWatchedSeconds() >= request.getTotalSeconds());
        progress.setUpdatedAt(LocalDateTime.now());
        repository.save(progress);
    }
}
