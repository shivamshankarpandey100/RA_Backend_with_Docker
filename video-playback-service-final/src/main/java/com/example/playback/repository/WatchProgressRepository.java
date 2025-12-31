
package com.example.playback.repository;

import com.example.playback.entity.WatchProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchProgressRepository extends JpaRepository<WatchProgress, Long> {
    Optional<WatchProgress> findByUserIdAndVideoId(String userId, String videoId);
    List<WatchProgress> findByUserId(String userId);
    List<WatchProgress> findByVideoId(String videoId);
    List<WatchProgress> findByUserIdAndCompleted(String userId, boolean completed);
}
