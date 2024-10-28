package com.huuduc.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRODUCT_KEY = "product::";
    private static final String PRODUCT_ALL_KEY = "product::all";

    @Override
    public void clear() {
        redisTemplate.delete(PRODUCT_ALL_KEY);
    }

    @Override
    public List<Product> getAll() throws JsonProcessingException {

        String json = (String) redisTemplate.opsForValue().get(PRODUCT_ALL_KEY);

        return json != null ?
                objectMapper.readValue(json, new TypeReference<>() {
                })
                : null;
    }

    @Override
    public ProductResponse getById(Integer id) {
        return null;
    }

    @Override
    public void saveAllProduct(List<Product> productList) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(productList);
        redisTemplate.opsForValue().set(PRODUCT_ALL_KEY,json);
        redisTemplate.expire(PRODUCT_ALL_KEY,10, TimeUnit.DAYS);
    }
}
