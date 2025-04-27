package com.example.saudi.post.controller;

import com.example.saudi.jwt.JwtUtil;
import com.example.saudi.post.dto.PostRequest;
import com.example.saudi.post.dto.PostResponse;
import com.example.saudi.post.service.PostService;
import com.example.saudi.user.entity.User;
import com.example.saudi.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        String oauthId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostResponse response = postService.createPost(request, user.getNickname(), user.getId());
        return ResponseEntity.ok(response);
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 단일 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
