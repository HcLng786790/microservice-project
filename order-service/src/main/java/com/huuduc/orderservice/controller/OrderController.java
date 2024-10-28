package com.huuduc.orderservice.controller;

import com.huuduc.orderservice.dto.ApiResponse;
import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.service.OrderService;
import com.huuduc.orderservice.user.UserClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrderController {

    private final OrderService orderService;
    private final UserClient userClient;

    /** Tạo đơn hàng mới (Không quản lý tiến trìng với Temporal)
     *
     * @param orderRequest {userId and list of purchase product}
     * @return OrderResponse with 201 code
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody @Valid OrderRequest orderRequest
    ){

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                null,
                this.orderService.createOrder(orderRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Tạo hơn hàng v2 (Quản lý tiến trình với Temporal)
     *
     * @param orderRequest:
     * @return OrderResponse
     */
    @PostMapping("/v2")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder2(
            @RequestBody @Valid OrderRequest orderRequest
    ){

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                null,
                this.orderService.createOrder2(orderRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Lấy thông tin đơn hàng của user hiện đang đăng nhập
     *
     * @return List<OrderResponse>
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrder(){

        ApiResponse<List<OrderResponse>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                null,
                this.orderService.getMyOrder()
        );

        return ResponseEntity.ok(apiResponse);
    }
}
