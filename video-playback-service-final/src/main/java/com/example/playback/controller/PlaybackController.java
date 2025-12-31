
package com.example.playback.controller;

import com.example.playback.dto.PlaybackRequest;
import com.example.playback.entity.PlaybackSession;
import com.example.playback.service.PlaybackService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playback")
public class PlaybackController {

    private final PlaybackService playbackService;

    public PlaybackController(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    @PostMapping("/start")
    public PlaybackSession start(@Valid @RequestBody PlaybackRequest request) {
        return playbackService.startSession(request.getUserId(), request.getVideoId());
    }

    @PostMapping("/heartbeat/{sessionId}")
    public void heartbeat(@PathVariable String sessionId) {
        playbackService.heartbeat(sessionId);
    }

    @PostMapping("/stop/{sessionId}")
    public void stop(@PathVariable String sessionId) {
        playbackService.stop(sessionId);
    }
}
