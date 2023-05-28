package com.example.posts.repository;

import com.example.posts.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByUserId(Integer userId, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO Post (id, user_id, title, body) VALUES (:id, :userId, :title, :body)",
            nativeQuery = true)
    void saveWithId(@Param("id") Integer id, @Param("userId") Integer userId, @Param("title") String title, @Param("body") String body);
}
