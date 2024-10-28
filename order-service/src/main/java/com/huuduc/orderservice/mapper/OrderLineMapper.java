package com.huuduc.orderservice.mapper;

import com.huuduc.orderservice.dto.OrderLineRequest;
import com.huuduc.orderservice.dto.OrderLineResponse;
import com.huuduc.orderservice.model.Order;
import com.huuduc.orderservice.model.OrderLine;
import org.springframework.stereotype.Component;

@Component
public class OrderLineMapper {

    public OrderLine toOrderLine(OrderLineRequest orderLineRequest){

        return OrderLine.builder()
                .productId(orderLineRequest.productId())
                .price(orderLineRequest.price())
                .quantity(orderLineRequest.quantity())
                .order(
                        Order.builder()
                                .id(orderLineRequest.orderId())
                                .build()
                )
                .build();
    }

    public OrderLineResponse fromOrderLine(OrderLine orderLine){

        return new OrderLineResponse(
                orderLine.getId(),
                orderLine.getProductId(),
                orderLine.getQuantity(),
                orderLine.getPrice()
        );
    }
}
