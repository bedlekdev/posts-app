package com.example.posts.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ExternalAPIService {
    <T> ResponseEntity<T> fetchExternalData(String url, Map<String, ?> uriVariables, Class<T> dto);
}
