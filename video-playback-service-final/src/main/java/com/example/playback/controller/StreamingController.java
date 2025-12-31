
package com.example.playback.controller;

import com.example.playback.service.StreamingService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stream")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Resource> stream(
            @PathVariable String videoId,
            @RequestHeader(value = "Range", required = false) String range
    ) {
        return streamingService.streamVideo(videoId, range);
    }
}
