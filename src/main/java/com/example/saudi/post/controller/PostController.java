package com.example.saudi.post.controller;

import com.example.saudi.jwt.JwtUtil;
import com.example.saudi.post.entity.Post;
import com.example.saudi.post.repository.PostRepository;
import com.example.saudi.user.entity.User;
import com.example.saudi.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findByOauthId(userId)  // 너가 만든 User 조회 방식에 맞게!
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이미지 업로드는 일단 생략
        List<String> imageUrls = new ArrayList<>(); // 추후 업로드 구현 시 대체

        Post post = Post.builder()
                .title(title)
                .content(content)
                .author(user.getNickname())
                .authorId(user.getId())
                .createdAt(LocalDateTime.now())
                .imageUrls(imageUrls)
                .build();

        postRepository.save(post);

        return ResponseEntity.ok(Map.of(
                "id", post.getId(),
                "message", "작성 완료"
        ));
    }
}
