package com.example.posts.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Post")
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_gen")
    @SequenceGenerator(name = "post_id_gen", sequenceName = "post_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Integer id;

    @NotNull(message = "User id  cannot be null")
    @Min(value = 1, message = "User id must be grater than 0")
    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @NotEmpty(message = "Title cannot be empty")
    @Column(name = "title", nullable = false)
    private String title;

    @NotEmpty(message = "Body cannot be empty")
    @Column(name = "body", nullable = false)
    private String body;

}
