
package com.example.playback.service;

import com.example.playback.entity.Video;
import com.example.playback.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository repository;

    public VideoService(VideoRepository repository) {
        this.repository = repository;
    }

    public List<Video> getAllVideos() {
        return repository.findAll();
    }
}
