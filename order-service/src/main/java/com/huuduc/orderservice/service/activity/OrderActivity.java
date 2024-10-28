package com.huuduc.orderservice.service.activity;

import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.dto.PurchaseRequest;
import com.huuduc.orderservice.model.Order;
import com.huuduc.orderservice.product.PurchaseResponse;
import com.huuduc.orderservice.user.UserResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface OrderActivity {

//    @ActivityMethod
//    OrderResponse createOrder(OrderRequest orderRequest,String token);

//    @ActivityMethod
//    OrderResponse createOrder2(OrderRequest orderRequest,String token,UserResponse userResponse, List<PurchaseResponse> purchaseResponseList);

    @ActivityMethod
    UserResponse getUser2(String token);

    @ActivityMethod
    List<PurchaseResponse> purchase2(List<PurchaseRequest> purchaseRequestList,String token);

    @ActivityMethod
    Order saveOrder(OrderRequest orderRequest,UserResponse userResponse,List<PurchaseResponse> purchaseResponseList);

    @ActivityMethod
    OrderResponse sendNotification(Order order,UserResponse userResponse,List<PurchaseResponse> purchaseResponseList);

    @ActivityMethod
    void cancelPurchase(List<PurchaseRequest> purchaseRequestList,String token);
}
