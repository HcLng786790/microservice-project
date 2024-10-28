package com.huuduc.orderservice.service.activity;

import com.huuduc.orderservice.dto.*;
import com.huuduc.orderservice.kafka.OrderProducer;
import com.huuduc.orderservice.mapper.OrderMapper;
import com.huuduc.orderservice.model.Order;
import com.huuduc.orderservice.product.ProductClient;
import com.huuduc.orderservice.product.PurchaseResponse;
import com.huuduc.orderservice.repository.OrderRepository;
import com.huuduc.orderservice.service.OrderLineService;
import com.huuduc.orderservice.user.UserClient;
import com.huuduc.orderservice.user.UserResponse;
import io.temporal.failure.ApplicationFailure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderActivityImpl implements OrderActivity{


    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Override
    public UserResponse getUser2(String token) {

        log.info("Conducting user testing");
        return this.userClient.getUser2(token);
    }

    @Override
    public List<PurchaseResponse> purchase2(List<PurchaseRequest> purchaseRequestList, String token) {
        try{

            log.info("Make a purchase");
            return this.productClient.purchase2(purchaseRequestList,token);
        }catch (Exception e){

            log.error("Exception: ",e);
            throw ApplicationFailure.newNonRetryableFailure("Purchase failed", e.getMessage());
        }
    }

    @Override
    public Order saveOrder(OrderRequest orderRequest, UserResponse userResponse, List<PurchaseResponse> purchaseResponseList) {

        //Lưu thông tin đơn hành
        var order = this.orderRepository.save(this.orderMapper.toOrder(orderRequest,userResponse.id()));

        // Tính tổng tiền
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PurchaseResponse purchaseResponse : purchaseResponseList) {

            var price = purchaseResponse.price().multiply(BigDecimal.valueOf(purchaseResponse.quantity()));

            totalPrice = totalPrice.add(price);

            this.orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            purchaseResponse.id(),
                            order.getId(),
                            purchaseResponse.quantity(),
                            purchaseResponse.price()
                    )
            );
        }

        order.setTotalPrice(totalPrice);
        this.orderRepository.save(order);

        return order;
    }

    @Transactional
    @Override
    public OrderResponse sendNotification(Order order,UserResponse userResponse,List<PurchaseResponse> purchaseResponseList) {
        try {

            // Gửi thông báo đến Notification (Activity)
            orderProducer.sendOrderConfirm(
                    new OrderConfirmation(
                            order.getOrderNumber(),
                            order.getTotalPrice(),
                            userResponse,
                            purchaseResponseList
                    )
            );

        return null;
//            return this.orderMapper.fromOrder(order);

        }catch (Exception e){
            throw ApplicationFailure.newNonRetryableFailure("Send failed", e.getMessage());
        }
    }

    @Override
    public void cancelPurchase(List<PurchaseRequest> purchaseRequestList, String token) {
        try{

            this.productClient.cancel(purchaseRequestList,token);
        }catch (Exception e){

            log.error("Exception: ",e);
            throw ApplicationFailure.newNonRetryableFailure("Cancel failed", e.getMessage());
        }
    }

/*
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest, String token) {

        //
        var user = getUser2(token);

        //purchase product
        var purchaseResponseList = purchase2(orderRequest.purchaseRequestList(),token);

        //Save the order and order line of this
        var order = this.orderRepository.save(this.orderMapper.toOrder(orderRequest,user.id()));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PurchaseResponse purchaseResponse : purchaseResponseList) {

            var price = purchaseResponse.price().multiply(BigDecimal.valueOf(purchaseResponse.quantity()));

            totalPrice = totalPrice.add(price);

            this.orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            purchaseResponse.id(),
                            order.getId(),
                            purchaseResponse.quantity(),
                            purchaseResponse.price()
                    )
            );
        }
        order.setTotalPrice(totalPrice);
        this.orderRepository.save(order);

        //send to notification
        orderProducer.sendOrderConfirm(
                new OrderConfirmation(
                        order.getOrderNumber(),
                        order.getTotalPrice(),
                        user,
                        purchaseResponseList
                )
        );
        //create order


//        return this.orderMapper.fromOrder(order);
        return null;
    }
    @Override
    public OrderResponse createOrder2(OrderRequest orderRequest, String token,UserResponse userResponse,List<PurchaseResponse> purchaseResponseList) {

        //Save the order and order line of this
        var order = this.orderRepository.save(this.orderMapper.toOrder(orderRequest,userResponse.id()));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PurchaseResponse purchaseResponse : purchaseResponseList) {

            var price = purchaseResponse.price().multiply(BigDecimal.valueOf(purchaseResponse.quantity()));

            totalPrice = totalPrice.add(price);

            this.orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            purchaseResponse.id(),
                            order.getId(),
                            purchaseResponse.quantity(),
                            purchaseResponse.price()
                    )
            );
        }
        order.setTotalPrice(totalPrice);
        this.orderRepository.save(order);

        //send to notification
        orderProducer.sendOrderConfirm(
                new OrderConfirmation(
                        order.getOrderNumber(),
                        order.getTotalPrice(),
                        userResponse,
                        purchaseResponseList
                )
        );


//        return this.orderMapper.fromOrder(order);
        return null;
    }
*/


}
