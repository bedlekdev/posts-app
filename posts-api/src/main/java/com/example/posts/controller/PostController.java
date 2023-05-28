package com.example.posts.controller;

import com.example.posts.dto.PostDTO;
import com.example.posts.model.Post;
import com.example.posts.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public Page<PostDTO> getPosts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(name = "user-id", required = false) Integer userId) {
        return postService.findAll(pageable, userId);
    }

    @GetMapping("{postId}")
    public PostDTO getPost(@PathVariable("postId") Integer postId) {
        return postService.getPost(postId);
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody @Valid Post post) {
        int createdId = postService.create(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable("postId") Integer postId,
            @RequestBody @Valid Post post) {
        return ResponseEntity.ok().body(postService.update(postId, post));
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Integer postId) {
        postService.deleteById(postId);
        return ResponseEntity.noContent().build();
    }
}
