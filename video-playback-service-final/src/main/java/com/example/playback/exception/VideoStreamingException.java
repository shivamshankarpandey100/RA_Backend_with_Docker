
package com.example.playback.exception;

public class VideoStreamingException extends RuntimeException {
    public VideoStreamingException(String message) {
        super(message);
    }

    public VideoStreamingException(String message, Throwable cause) {
        super(message, cause);
    }
}
