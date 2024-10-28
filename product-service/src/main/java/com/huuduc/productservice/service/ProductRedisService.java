package com.huuduc.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductRedisService {

    void clear();

    List<Product> getAll() throws JsonProcessingException;

    @SuppressWarnings("unused")
    ProductResponse getById(Integer id);

    void saveAllProduct(List<Product> productList) throws JsonProcessingException;
}
