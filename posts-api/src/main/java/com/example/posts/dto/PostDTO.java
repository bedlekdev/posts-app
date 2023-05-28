package com.example.posts.dto;

public record PostDTO(
        Integer id,
        Integer userId,
        String title,
        String body
) {
}
