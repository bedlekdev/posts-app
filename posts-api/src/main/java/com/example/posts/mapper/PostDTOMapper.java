package com.example.posts.mapper;

import com.example.posts.dto.PostDTO;
import com.example.posts.model.Post;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PostDTOMapper implements Function<Post, PostDTO> {

    @Override
    public PostDTO apply(Post post) {
        return new PostDTO(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getBody());
    }
}
