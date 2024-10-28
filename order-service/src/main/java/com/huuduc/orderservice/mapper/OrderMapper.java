package com.huuduc.orderservice.mapper;

import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderMapper {

    public final OrderLineMapper orderLineMapper;

    public Order toOrder(OrderRequest orderRequest,Integer userId){

        return Order.builder()
                .orderNumber("random number")
                .userId(userId)
                .build();
    }

    public OrderResponse fromOrder(Order order){

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getCreatedDate(),
                order.getLastModifiedDate(),
                order.getOrderLineList().stream()
                        .map(this.orderLineMapper::fromOrderLine).toList()

        );
    }
}
