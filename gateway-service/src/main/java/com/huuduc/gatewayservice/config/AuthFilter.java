package com.huuduc.gatewayservice.config;

import com.huuduc.gatewayservice.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SuppressWarnings("unused")
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final AuthService authService;
    private final String[] publicEndpoints = {
            "/api/auth/authenticate",
            "/api/auth/register",
            "/api/auth/verify",
            "/actuator/prometheus",
            "/api-docs",
            "/swagger-ui.html",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/aggregate/auth-service/v3/api-docs",
            "/aggregate/product-service/v3/api-docs",
            "/aggregate/order-service/v3/api-docs"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Ghi log thông tin khi vào bộ lọc xác thực
        log.info("Enter authentication filter");

        // Nếu là các endpoint công khai thì không cần lọc
        if (isPublicEndpoints(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // Lấy header "Authorization" từ yêu cầu
        List<String> authHeader = exchange.getRequest().getHeaders().get(AUTHORIZATION);

        // Nếu không có header Authorization hoặc danh sách rỗng, trả về mã 401 (Unauthorized)
        if (CollectionUtils.isEmpty(authHeader)) {
            return unauthenticated(exchange.getResponse());
        }

        // Lấy token từ header Authorization (bỏ tiền tố "Bearer ")
        String token = authHeader.get(0).substring(7);
        log.info("Token: {}", token);

        // Xác thực token
        return authService.verify(token).flatMap(verifyResponseApiResponse -> {
            if (verifyResponseApiResponse.getResult().valid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    private boolean isPublicEndpoints(ServerHttpRequest request) {

        return Arrays.stream(publicEndpoints).anyMatch(s -> request.getURI().getPath().matches(s));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {

        String body = "Invalid token";
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
