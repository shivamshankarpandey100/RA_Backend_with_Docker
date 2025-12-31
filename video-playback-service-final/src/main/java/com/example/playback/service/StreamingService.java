
package com.example.playback.service;

import com.example.playback.exception.ResourceNotFoundException;
import com.example.playback.exception.VideoStreamingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StreamingService {

    @Value("${video.storage.path:videos}")
    private String videoStoragePath;

    public ResponseEntity<Resource> streamVideo(String videoId, String range) {
        validateVideoId(videoId);

        try {
            File file = getSecureVideoFile(videoId);

            if (!file.exists() || !file.isFile()) {
                throw new ResourceNotFoundException("Video file not found: " + videoId);
            }

            long length = file.length();
            long start = 0, end = length - 1;

            if (range != null && range.startsWith("bytes=")) {
                String[] parts = range.replace("bytes=", "").split("-");
                start = Long.parseLong(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    end = Long.parseLong(parts[1]);
                }

                if (start > end || start < 0 || end >= length) {
                    throw new VideoStreamingException("Invalid range request");
                }
            }

            long contentLength = end - start + 1;

            try (FileInputStream fis = new FileInputStream(file)) {
                long skipped = fis.skip(start);
                if (skipped != start) {
                    throw new VideoStreamingException("Failed to skip to requested position");
                }

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Range", "bytes " + start + "-" + end + "/" + length);
                headers.add("Accept-Ranges", "bytes");
                headers.setContentLength(contentLength);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .contentType(MediaType.valueOf("video/mp4"))
                        .body(new InputStreamResource(fis));
            }

        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Video file not found: " + videoId);
        } catch (IOException e) {
            throw new VideoStreamingException("Error streaming video: " + videoId, e);
        } catch (NumberFormatException e) {
            throw new VideoStreamingException("Invalid range format", e);
        }
    }

    private void validateVideoId(String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) {
            throw new VideoStreamingException("Video ID cannot be null or empty");
        }

        if (videoId.contains("..") || videoId.contains("/") || videoId.contains("\\")) {
            throw new VideoStreamingException("Invalid video ID format");
        }

        if (!videoId.matches("^[a-zA-Z0-9_-]+$")) {
            throw new VideoStreamingException("Video ID contains invalid characters");
        }
    }

    private File getSecureVideoFile(String videoId) throws IOException {
        Path basePath = Paths.get(videoStoragePath).toAbsolutePath().normalize();
        Path videoPath = basePath.resolve(videoId + ".mp4").normalize();

        if (!videoPath.startsWith(basePath)) {
            throw new VideoStreamingException("Path traversal attempt detected");
        }

        return videoPath.toFile();
    }
}
