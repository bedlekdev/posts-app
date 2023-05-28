package com.example.posts.service.implementation;

import com.example.posts.dto.PostDTO;
import com.example.posts.dto.UserDTO;
import com.example.posts.exception.ResourceNotChangedException;
import com.example.posts.exception.ResourceNotFoundException;
import com.example.posts.mapper.PostDTOMapper;
import com.example.posts.model.Post;
import com.example.posts.repository.PostRepository;
import com.example.posts.service.ExternalAPIService;
import com.example.posts.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@AllArgsConstructor
@Transactional
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ExternalAPIService externalAPIService;
    private final PostDTOMapper postDTOMapper;

    private final JdbcTemplate jdbcTemplate;

    private static ResourceNotFoundException getResourceNotFoundExceptionForPost(Integer id) {
        return new ResourceNotFoundException("Post with id: [%s] was not found.".formatted(id));
    }

    @Override
    public Integer create(Post post) {
        log.info("Saving new post with title: {}", post.getTitle());
        if (!isUserIdValid(post.getUserId())) {
            throw new ResourceNotFoundException("User with id: [%s] was not found.".formatted(post.getUserId()));
        }
        return postRepository.save(post).getId();
    }

    private boolean isUserIdValid(Integer userId) {
        try {
            ResponseEntity<UserDTO> response = externalAPIService.fetchExternalData("/users/{id}", Map.of("id", userId), UserDTO.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception ex) {
            if (!(ex instanceof HttpClientErrorException.NotFound)) {
                log.error("User id: [%s] is not valid".formatted(userId), ex);
            }
            return false;
        }
    }

    @Override
    public PostDTO update(Integer id, Post updatedPost) {
        log.info("Updating post with id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> getResourceNotFoundExceptionForPost(id));

        boolean changed = false;
        if (!updatedPost.getTitle().equals(post.getTitle())) {
            post.setTitle(updatedPost.getTitle());
            changed = true;
        }
        if (!updatedPost.getBody().equals(post.getBody())) {
            post.setBody(updatedPost.getBody());
            changed = true;
        }
        if (!changed) {
            throw new ResourceNotChangedException();
        }
        return postDTOMapper.apply(postRepository.save(post));
    }

    @Override
    public void deleteById(Integer id) {
        log.info("Deleting post with id: {}", id);
        checkIfPostExistsOrThrowResourceNotFoundException(id);
        postRepository.deleteById(id);
    }

    private void checkIfPostExistsOrThrowResourceNotFoundException(Integer id) {
        if (postRepository.findById(id).isEmpty()) {
            throw getResourceNotFoundExceptionForPost(id);
        }
    }

    @Override
    public PostDTO getPost(Integer id) {
        return postRepository.findById(id)
                .map(postDTOMapper)
                .orElseGet(() -> findPostByExternalApiAndUpdateSequence(id));
    }

    private PostDTO findPostByExternalApiAndUpdateSequence(Integer id) {
        PostDTO foundPostDTO = getPostFromExternalApi(id);
        postRepository.saveWithId(foundPostDTO.id(), foundPostDTO.userId(), foundPostDTO.title(),
                foundPostDTO.body());
        jdbcTemplate.execute("SELECT setval('post_id_seq', (SELECT MAX(id) FROM post))");
        return foundPostDTO;
    }

    private PostDTO getPostFromExternalApi(Integer postId) {
        try {
            ResponseEntity<PostDTO> response = externalAPIService.fetchExternalData(
                    "/posts/{id}",
                    Map.of("id", postId),
                    PostDTO.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception ex) {
            log.error("Post with id: [%s] was not found by external api.".formatted(postId), ex);
        }
        throw getResourceNotFoundExceptionForPost(postId);
    }

    @Override
    public Page<PostDTO> findAll(Pageable pageable, Integer userId) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
        return userId != null ?
                postRepository.findAllByUserId(userId, pageable).map(postDTOMapper) :
                postRepository.findAll(pageable).map(postDTOMapper);
    }
}
