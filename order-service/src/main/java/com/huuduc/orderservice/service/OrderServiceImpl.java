package com.huuduc.orderservice.service;

import com.huuduc.orderservice.dto.OrderConfirmation;
import com.huuduc.orderservice.dto.OrderLineRequest;
import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.exception.BusinessException;
import com.huuduc.orderservice.kafka.OrderProducer;
import com.huuduc.orderservice.mapper.OrderMapper;
import com.huuduc.orderservice.product.ProductClient;
import com.huuduc.orderservice.product.PurchaseResponse;
import com.huuduc.orderservice.repository.OrderRepository;
import com.huuduc.orderservice.service.workflow.OrderWorkflow;
import com.huuduc.orderservice.user.UserClient;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrderServiceImpl implements OrderService {


    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final WorkflowClient workflowClient;
    private final HttpServletRequest request;

    @Transactional
    @Override
    public OrderResponse createOrder2(OrderRequest orderRequest) {

        // Lấy token từ header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authHeader != null ? authHeader.substring(7) : null; // Loại bỏ "Bearer "

        if (token == null) {
            throw new BusinessException("Authorization token is missing");
        }

        // Định nghĩa tùy chọn Workflow
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("OrderTaskQueue")
                .build();

        // Khởi tạo và gọi workflow
        OrderWorkflow orderWorkflow = this.workflowClient.newWorkflowStub(OrderWorkflow.class,options);
        return orderWorkflow.createOrderWorkflow(orderRequest,token);

    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {

        // Kiểm tra user
        var user = this.userClient.getUser();

        // Mua hàng
        var purchaseResponseList = this.productClient.purchase(orderRequest.purchaseRequestList());

        // Lưu thông tin đơn hàng
        var order = this.orderRepository.save(this.orderMapper.toOrder(orderRequest,user.id()));

        // Tính toán tổng tiền
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

        // Gửi thông báo đến Notification-Service
        orderProducer.sendOrderConfirm(
                new OrderConfirmation(
                        order.getOrderNumber(),
                        order.getTotalPrice(),
                        user,
                        purchaseResponseList
                )
        );

//        return this.orderMapper.fromOrder(order);
        return null;
    }

    @Override
    public List<OrderResponse> getMyOrder() {

        //Kiểm tra user hiện tại
        var findUser = this.userClient.getUser();

        var orderList = this.orderRepository.findByUserId(findUser.id());

        return orderList
                .stream()
                .map(this.orderMapper::fromOrder)
                .toList();
    }
}
