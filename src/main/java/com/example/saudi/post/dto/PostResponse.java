package com.example.saudi.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Long authorId;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}