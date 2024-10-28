package com.huuduc.orderservice.service;

import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.model.Order;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    OrderResponse createOrder(@Valid OrderRequest orderRequest);

    List<OrderResponse> getMyOrder();

    OrderResponse createOrder2(@Valid OrderRequest orderRequest);

}
