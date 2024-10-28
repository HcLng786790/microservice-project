package com.huuduc.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huuduc.productservice.dto.ProductRequest;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.dto.PurchaseRequest;
import com.huuduc.productservice.dto.PurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    ProductResponse create(@Valid ProductRequest productRequest);

    List<ProductResponse> getAll() throws JsonProcessingException;

    ProductResponse getById(Integer id);

    List<PurchaseResponse> purchase(@Valid List<PurchaseRequest> purchaseRequestList);

    ProductResponse update(Integer productId, ProductRequest productRequest);

    void cancel(List<PurchaseRequest> purchaseRequestList);
}
