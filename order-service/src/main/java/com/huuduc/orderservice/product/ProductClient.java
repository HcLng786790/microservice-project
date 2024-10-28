package com.huuduc.orderservice.product;

import com.huuduc.orderservice.dto.ApiResponse;
import com.huuduc.orderservice.dto.PurchaseRequest;
import com.huuduc.orderservice.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.temporal.failure.ApplicationFailure;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClient {

    private static final String PRODUCT_SERVICE = "product-service";


    @SuppressWarnings("unused")
    @Value("${application.config.product-url}")
    private String productUrl;
    private final RestTemplate restTemplate;
    private final HttpServletRequest request; // Để lấy token từ yêu cầu hiện tại


    /**
     * Gọi tới api purchase của product-service
     *
     * @param purchaseRequestList The list of product purchase
     * @return List of purchaseResponse
     */
    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "fallbackMethod")
    public List<PurchaseResponse> purchase(List<PurchaseRequest> purchaseRequestList) {

        // Tạo http header
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE); // Set content type as JSON

        // Lấy token ra từ header (Nếu dùng Temporal thì phương thức này gây ra lỗi)
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authHeader != null ? authHeader.substring(7) : null;

        if (token == null) {
            throw new BusinessException("Authorization token is missing");
        }

        // Thêm token vào header request
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // Tạo request
        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequestList, headers);

        // Sử dụng ParameterizedTypeReference để xử lý các kiểu chung như ApiResponse<List<PurchaseResponse>>
        ParameterizedTypeReference<ApiResponse<List<PurchaseResponse>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        try {
            // Gọi tới purchase api của Product-Service
            ResponseEntity<ApiResponse<List<PurchaseResponse>>> responseEntity = this.restTemplate.exchange(
                    productUrl + "/purchase",
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );

            // Lấy kết quả trả về từ Rest template
            ApiResponse<List<PurchaseResponse>> apiResponse = responseEntity.getBody();

            // Ensure the response body is not null
            if (apiResponse == null || apiResponse.getResult() == null) {
                throw new BusinessException("Invalid response from product-service");
            }

            return apiResponse.getResult();

        } catch (HttpClientErrorException e) {

            // Exception handling 4xx
            throw new BusinessException("Client error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {

            // Exception handling 5xx
            throw new BusinessException("Server error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {

            // Exception handling other
            throw new BusinessException("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Hàm gọi purchase v2 với việc truyền token trước thay vì lấy token từ header trong function
    // Vì việc dùng temporal không cùng luồng với Servlet nên không thể lấy token ra được
    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "fallbackMethod")
    public List<PurchaseResponse> purchase2(List<PurchaseRequest> purchaseRequestList, String token) {

        // Tạo http header
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE); // Set content type as JSON

        if (token == null) {
            throw new BusinessException("Authorization token is missing");
        }

        // Thêm token vào header request
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // Tạo request
        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequestList, headers);

        // Sử dụng ParameterizedTypeReference để xử lý các kiểu chung như ApiResponse<List<PurchaseResponse>>
        ParameterizedTypeReference<ApiResponse<List<PurchaseResponse>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        try {
            // Gọi API purchase từ Product Service
            ResponseEntity<ApiResponse<List<PurchaseResponse>>> responseEntity = this.restTemplate.exchange(
                    productUrl + "/purchase",
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );

            // Lấy kết quả từ API
            ApiResponse<List<PurchaseResponse>> apiResponse = responseEntity.getBody();

            // Ensure the response body is not null
            if (apiResponse == null || apiResponse.getResult() == null) {
                throw new BusinessException("Invalid response from product-service");
            }

            return apiResponse.getResult();

        } catch (HttpClientErrorException e) {

            // Exception handling 4xx
            throw ApplicationFailure.newNonRetryableFailure("Client error occurred: ", e.getMessage());
        } catch (HttpServerErrorException e) {

            // Exception handling 5xx
            throw new BusinessException("Server error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {

            // Exception handling other
            throw new BusinessException("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void cancel(List<PurchaseRequest> purchaseRequestList, String token) {

        // Tạo header
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE); // Set content type as JSON

        if (token == null) {
            throw new BusinessException("Authorization token is missing");
        }

        // Thêm token vào header
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // Tạo request Entity
        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequestList, headers);

        try {

            this.restTemplate.exchange(
                    productUrl + "/cancel",
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );

        } catch (HttpClientErrorException e) {

            // Exception handling 4xx
            throw ApplicationFailure.newNonRetryableFailure("Client error occurred: ", e.getMessage());
        } catch (HttpServerErrorException e) {

            // Exception handling 5xx
            throw new BusinessException("Server error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {

            // Exception handling other
            throw new BusinessException("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Hàm gọi FallBack từ hệ thống chịu lỗi
    @SuppressWarnings("unused")
    public List<PurchaseResponse> fallbackMethod(List<PurchaseRequest> purchaseRequestList, String token, Throwable throwable) {

        if (log.isInfoEnabled()) {
            log.info("Product Service has unavailable::{}", throwable.toString());
        }
        throw new BusinessException("Product Service has unavailable::" + throwable.toString());
    }
}
