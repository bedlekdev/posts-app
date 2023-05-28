package com.example.posts.service;

import com.example.posts.dto.PostDTO;
import com.example.posts.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Integer create(Post post);
    
    PostDTO update(Integer id, Post post);

    void deleteById(Integer id);

    PostDTO getPost(Integer id);

    Page<PostDTO> findAll(Pageable pageable, Integer userId);

}
