package com.huuduc.productservice.model;

import com.huuduc.productservice.service.ProductRedisService;
import jakarta.persistence.PostUpdate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ProductListener {

    private final ProductRedisService productRedisService;

    @PostUpdate
    public void postUpdate(Product product){
        log.info("postUpdate");
        productRedisService.clear();
    }
}
