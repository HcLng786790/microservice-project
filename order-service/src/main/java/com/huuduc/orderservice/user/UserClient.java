package com.huuduc.orderservice.user;

import com.huuduc.orderservice.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClient {

    @SuppressWarnings("unused")
    @Value("${application.config.user-url}")
    private String userUrl;
    private final RestTemplate restTemplate;
    private final HttpServletRequest request; // Để lấy token từ yêu cầu hiện tại

    // Dùng cho order ko quản lý Temopral
    public UserResponse getUser() {
        try {

            // Get token from header
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = authHeader != null ? authHeader.substring(7) : null; // Loại bỏ "Bearer "

            // Create header http with token
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            // Create Http Entity
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            // Call the user-service API
            ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, HttpMethod.GET,entity,UserResponse.class);

            // Check if the response indicates an error
            if (response.getStatusCode().isError()) {
                throw new BusinessException("An error occurred while processing the user get API: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            // Handle any other exceptions that might occur
            throw new BusinessException("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Dùng cho order có quản lý Temporal
    public UserResponse getUser2(String token) {
        try {

            // Create header http with token
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            // Create Http Entity
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            // Call the user-service API
            ResponseEntity<UserResponse> response = restTemplate.exchange(userUrl, HttpMethod.GET,entity,UserResponse.class);

            // Check if the response indicates an error
            if (response.getStatusCode().isError()) {
                throw new BusinessException("An error occurred while processing the user get API: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            // Handle any other exceptions that might occur
            throw new BusinessException("An unexpected error occurred: " + e.getMessage());
        }
    }
}
