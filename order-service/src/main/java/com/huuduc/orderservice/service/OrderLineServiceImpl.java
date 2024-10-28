package com.huuduc.orderservice.service;

import com.huuduc.orderservice.dto.OrderLineRequest;
import com.huuduc.orderservice.mapper.OrderLineMapper;
import com.huuduc.orderservice.repository.OrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrderLineServiceImpl implements OrderLineService{

    private final OrderLineMapper orderLineMapper;
    private final OrderLineRepository orderLineRepository;

    @Override
    public void saveOrderLine(OrderLineRequest orderLineRequest) {

        var orderLine = this.orderLineMapper.toOrderLine(orderLineRequest);

        this.orderLineRepository.save(orderLine);
    }
}
