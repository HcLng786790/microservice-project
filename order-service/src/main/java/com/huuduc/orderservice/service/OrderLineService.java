package com.huuduc.orderservice.service;

import com.huuduc.orderservice.dto.OrderLineRequest;
import org.springframework.stereotype.Service;

@Service
public interface OrderLineService {

    void saveOrderLine(OrderLineRequest orderLineRequest);
}
