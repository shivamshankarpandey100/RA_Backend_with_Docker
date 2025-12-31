
package com.example.playback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "videos")
public class Video {
    @Id
    private String id;
    private String title;
    private String filePath;
    private String mimeType;
    private Long duration;
    private String quality;
}
