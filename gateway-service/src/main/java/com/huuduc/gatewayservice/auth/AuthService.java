package com.huuduc.gatewayservice.auth;

import com.huuduc.gatewayservice.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    public Mono<ApiResponse<VerifyResponse>> verify(String token){

        return this.authClient.verify(new VerifyRequest(token));
    }
}
