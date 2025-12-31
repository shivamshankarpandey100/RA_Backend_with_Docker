
package com.example.playback.controller;

import com.example.playback.entity.Video;
import com.example.playback.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<Video> getAll() {
        return videoService.getAllVideos();
    }
}
