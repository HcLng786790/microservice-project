package com.huuduc.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@SuppressWarnings("unused")
public class RestTemplateConfig {

    // Cấu hình Rest Template
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
