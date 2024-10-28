package com.huuduc.gatewayservice.auth;

import com.huuduc.gatewayservice.dto.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface AuthClient {

    @PostExchange(url="/api/auth/verify",contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<VerifyResponse>> verify(@RequestBody VerifyRequest verifyRequest);
}
