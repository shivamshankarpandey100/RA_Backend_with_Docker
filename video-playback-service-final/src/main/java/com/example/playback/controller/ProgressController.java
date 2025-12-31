
package com.example.playback.controller;

import com.example.playback.dto.ProgressRequest;
import com.example.playback.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/update")
    public void update(@Valid @RequestBody ProgressRequest request) {
        progressService.updateProgress(request);
    }
}
