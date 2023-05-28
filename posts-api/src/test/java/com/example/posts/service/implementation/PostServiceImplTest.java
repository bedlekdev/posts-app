package com.example.posts.service.implementation;

import com.example.posts.dto.*;
import com.example.posts.exception.ResourceNotChangedException;
import com.example.posts.exception.ResourceNotFoundException;
import com.example.posts.mapper.PostDTOMapper;
import com.example.posts.model.Post;
import com.example.posts.repository.PostRepository;
import com.example.posts.service.ExternalAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private final PostDTOMapper postDTOMapper = new PostDTOMapper();
    @Mock
    private PostRepository postRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ExternalAPIService externalAPIService;
    private PostServiceImpl postService;
    private Post post;

    private static Page<Post> getPageForFindAll(List<Post> posts) {
        PageRequest pageRequest = PageRequest.of(0, 20);
        return new PageImpl<>(posts, pageRequest, posts.size());
    }

    private static UserDTO mockUserDTO() {
        return new UserDTO(
                1,
                "name",
                "username",
                "email",
                new AddressDTO("street", "suite", "city", "zipcode", new GeoDTO("lat", "lng")),
                "phone",
                "website",
                new CompanyDTO("name", "catchPhrase", "bs"));
    }

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, externalAPIService, postDTOMapper, jdbcTemplate);
        post = new Post(1, 10, "Test title", "TestBody");
    }

    @DisplayName("JUnit test for create post")
    @Test
    public void givenPost_whenCreatePost_thenReturnCreatedPostId() {
        // given
        given(externalAPIService.fetchExternalData(anyString(), anyMap(), any()))
                .willReturn(ResponseEntity.ok(mockUserDTO()));
        given(postRepository.save(post)).willReturn(post);

        // when
        int id = postService.create(post);

        // then
        assertThat(id).isEqualTo(post.getId());
        verify(externalAPIService, times(1)).fetchExternalData(anyString(), anyMap(), any());
    }

    @DisplayName("JUnit test for create which throws ResourceNotFoundException")
    @Test
    public void givenNonValidUserId_whenCreatePost_thenThrowsResourceNotFoundException() {
        // given
        given(externalAPIService.fetchExternalData(anyString(), anyMap(), any()))
                .willThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "not found", HttpHeaders.EMPTY, null,
                        null));

        // when
        // then
        assertThatThrownBy(() -> postService.create(post))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id: [%s] was not found.".formatted(post.getUserId()));
        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("JUnit test for update title and body")
    @Test
    public void givenPost_whenUpdateTitleAndBody_thenReturnUpdatedPostDTO() {
        // given
        String title = "Changed title";
        String body = "Changed body";
        Post updatedPost = new Post(post.getId(), post.getUserId(), title, body);
        given(postRepository.findById(anyInt())).willReturn(Optional.of(post));
        given(postRepository.save(any(Post.class))).willReturn(updatedPost);

        // when
        PostDTO updatedPostDTO = postService.update(post.getId(), updatedPost);

        // then
        assertThat(updatedPostDTO).isNotNull();
        assertThat(updatedPostDTO.id()).isEqualTo(post.getId());
        assertThat(updatedPostDTO.userId()).isEqualTo(post.getUserId());
        assertThat(updatedPostDTO.title()).isEqualTo(title);
        assertThat(updatedPostDTO.body()).isEqualTo(body);
    }

    @DisplayName("JUnit test for update title")
    @Test
    public void givenPost_whenUpdateTitle_thenReturnUpdatedPostDTO() {
        // given
        String title = "Changed title";
        Post updatedPost = new Post(post.getId(), post.getUserId(), title, post.getBody());
        given(postRepository.findById(anyInt())).willReturn(Optional.of(post));
        given(postRepository.save(any(Post.class))).willReturn(updatedPost);

        // when
        PostDTO updatedPostDTO = postService.update(post.getId(), updatedPost);

        // then
        assertThat(updatedPostDTO).isNotNull();
        assertThat(updatedPostDTO.id()).isEqualTo(post.getId());
        assertThat(updatedPostDTO.userId()).isEqualTo(post.getUserId());
        assertThat(updatedPostDTO.title()).isEqualTo(title);
        assertThat(updatedPostDTO.body()).isEqualTo(post.getBody());
    }

    @DisplayName("JUnit test for update body")
    @Test
    public void givenPost_whenUpdateBody_thenReturnUpdatedPostDTO() {
        // given
        String body = "Changed body";
        Post updatedPost = new Post(post.getId(), post.getUserId(), post.getTitle(), body);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(postRepository.save(any(Post.class))).willReturn(updatedPost);

        // when
        PostDTO updatedPostDTO = postService.update(post.getId(), updatedPost);

        // then
        assertThat(updatedPostDTO).isNotNull();
        assertThat(updatedPostDTO.id()).isEqualTo(post.getId());
        assertThat(updatedPostDTO.userId()).isEqualTo(post.getUserId());
        assertThat(updatedPostDTO.title()).isEqualTo(post.getTitle());
        assertThat(updatedPostDTO.body()).isEqualTo(body);
    }

    @DisplayName("JUnit test for update which throws ResourceNotFoundException")
    @Test
    public void givenNonExistingPostId_whenUpdate_thenThrowsResourceNotFoundException() {
        // given
        int id = 1;
        given(postRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> postService.update(id, post))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id: [%s] was not found.".formatted(id));
        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("JUnit test for update which throws ResourceNotChangedException")
    @Test
    public void givenUnchangedPost_whenUpdate_thenThrowsResourceNotChangedException() {
        // given
        given(postRepository.findById(anyInt())).willReturn(Optional.of(post));

        // when
        // then
        assertThatThrownBy(() -> postService.update(1, post))
                .isInstanceOf(ResourceNotChangedException.class)
                .hasMessage("Resource was not changed");
        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("JUnit test for deleteById")
    @Test
    public void givenPostId_whenDeleteById_thenNothing() {
        // given
        int id = 1;
        given(postRepository.findById(id)).willReturn(Optional.of(post));
        willDoNothing().given(postRepository).deleteById(id);

        // when
        postService.deleteById(id);

        // then
        verify(postRepository, times(1)).deleteById(id);
    }

    @DisplayName("JUnit test for deleteById which throws ResourceNotFoundException")
    @Test
    public void givenNonExistingPostId_whenDeleteById_thenThrowsResourceNotFoundException() {
        // given
        int id = 1;
        given(postRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> postService.deleteById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id: [%s] was not found.".formatted(id));
        verify(postRepository, never()).deleteById(anyInt());
    }

    @DisplayName("JUnit test for getPost by id")
    @Test
    public void givenPostId_whenGetPost_thenReturnPostDTO() {
        // given
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        // when
        PostDTO postDTO = postService.getPost(post.getId());

        // then
        assertThat(postDTO).isNotNull();
        assertThat(postDTO.id()).isEqualTo(post.getId());
    }

    @DisplayName("JUnit test for getPost by id which find post in external api an save post and return post")
    @Test
    public void givenNonExistingPostId_whenGetPost_thenFindWithExternalApiAndSavePostAndReturnPost() {
        // given
        given(postRepository.findById(post.getId())).willReturn(Optional.empty());
        given(externalAPIService.fetchExternalData(anyString(), anyMap(), any()))
                .willReturn(ResponseEntity.ok(postDTOMapper.apply(post)));

        // when
        PostDTO postDTO = postService.getPost(post.getId());

        // then
        assertThat(postDTO).isNotNull();
        assertThat(postDTO.id()).isEqualTo(post.getId());
        verify(postRepository, times(1))
                .saveWithId(post.getId(), post.getUserId(), post.getTitle(), post.getBody());
        verify(postRepository, times(1)).findById(anyInt());
    }

    @DisplayName("JUnit test for getPost by id which throws ResourceNotFoundException")
    @Test
    public void givenNonExistingPostId_whenGetPost_thenThrowsResourceNotFoundException() {
        // given
        int id = 1;
        given(postRepository.findById(id)).willReturn(Optional.empty());
        given(externalAPIService.fetchExternalData(anyString(), anyMap(), any()))
                .willThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "not found", HttpHeaders.EMPTY, null, null));

        // when
        // then
        assertThatThrownBy(() -> postService.getPost(post.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post with id: [%s] was not found.".formatted(id));
        verify(postRepository, times(1)).findById(anyInt());
        verify(externalAPIService, times(1)).fetchExternalData(anyString(), anyMap(), any());
    }

    @DisplayName("JUnit test for findAll(Pageable pageable)")
    @Test
    public void givenPageAndUserIdNull_whenFindAll_thenReturnPage() {
        // given
        List<Post> posts = List.of(post, new Post(2, 15, "Title second", "Body second"));
        Page<Post> page = getPageForFindAll(posts);
        given(postRepository.findAll(page.getPageable())).willReturn(page);
        // when
        Page<PostDTO> pagePostDTOs = postService.findAll(page.getPageable(), null);

        // then
        assertThat(pagePostDTOs).isNotNull();
        assertThat(pagePostDTOs.getTotalElements()).isEqualTo(posts.size());
        verify(postRepository, times(1)).findAll(page.getPageable());
        verify(postRepository, never()).findAllByUserId(anyInt(), any(Pageable.class));
    }

    @DisplayName("JUnit test for findAllByUserId(Integer userId, Pageable pageable)")
    @Test
    public void givenPageAndUserIdNotNull_whenFindAll_thenReturnPage() {
        // given
        List<Post> posts = List.of(post, new Post(2, post.getUserId(), "Title second", "Body second"));
        Page<Post> page = getPageForFindAll(posts);
        given(postRepository.findAllByUserId(post.getUserId(), page.getPageable())).willReturn(page);
        // when
        Page<PostDTO> pagePostDTOs = postService.findAll(page.getPageable(), post.getUserId());

        // then
        assertThat(pagePostDTOs).isNotNull();
        assertThat(pagePostDTOs.getTotalElements()).isEqualTo(posts.size());
        verify(postRepository, times(1)).findAllByUserId(post.getUserId(), page.getPageable());
        verify(postRepository, never()).findAll(page.getPageable());
    }
}