package com.example.posts.service.implementation;

import com.example.posts.service.ExternalAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
    private final RestTemplate restTemplate;

    @Value("${external-api.base-url}")
    private String baseUrl;

    @Override
    public <T> ResponseEntity<T> fetchExternalData(String url, Map<String, ?> uriVariables, Class<T> dtoClass) {
        return restTemplate.getForEntity(baseUrl + url, dtoClass, uriVariables);
    }
}
