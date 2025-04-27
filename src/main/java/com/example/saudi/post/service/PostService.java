package com.example.saudi.post.service;

import com.example.saudi.post.dto.PostRequest;
import com.example.saudi.post.dto.PostResponse;
import com.example.saudi.post.entity.Post;
import com.example.saudi.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 생성
    public PostResponse createPost(PostRequest request, String author, Long authorId) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .authorId(authorId)
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    // 게시글 단건 조회
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다: id=" + id));

        return toResponse(post);
    }

    // 게시글 전체 조회
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 게시글 수정
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다: id=" + id));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now()); // 간단하게 createdAt 업데이트 (별도 updatedAt 컬럼이 있다면 그걸 쓰는 게 좋음)

        return toResponse(postRepository.save(post));
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 게시글이 없습니다: id=" + id);
        }
        postRepository.deleteById(id);
    }

    // Entity -> DTO 변환
    private PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .authorId(post.getAuthorId())
                .createdAt(post.getCreatedAt())
                .imageUrls(post.getImageUrls())
                .build();
    }
}


