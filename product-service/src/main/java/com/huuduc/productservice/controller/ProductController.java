package com.huuduc.productservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huuduc.productservice.dto.*;
import com.huuduc.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Tạo sản phẩm mới (quyền ADMIN)
     *
     * @param productRequest:
     * @return productResponse
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestBody @Valid ProductRequest productRequest
    ){

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(
                CREATED,null,this.productService.create(productRequest)
        );

        return ResponseEntity.status(CREATED).body(apiResponse);
    }

    /**
     * Lấy danh sách tất cả sản phẩm
     *
     * @return Product Response:
     * @throws JsonProcessingException:
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() throws JsonProcessingException {

        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>(
                OK,null,this.productService.getAll()
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Lấy thông tin của 1 sản phẩm theo ID
     *
     * @param id:
     * @return productResponse
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable(name = "productId") Integer id
    ){

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(
                OK,null,this.productService.getById(id)
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Cập nhật thông tin của 1 sản phẩm theo id (quyền ADMIN)
     *
     * @param productId:
     * @param productRequest:
     * @return productResponse
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable(name = "productId") Integer productId,
            @RequestBody ProductRequest productRequest
    ){
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(
                OK,null,this.productService.update(productId,productRequest)
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Mua sản phẩm (Order Service gọi tới để giảm số lượng)
     *
     * @param purchaseRequestList:
     * @return purchaseResponse List
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<List<PurchaseResponse>>> purchase(
            @RequestBody @Valid List<PurchaseRequest> purchaseRequestList
    ){

        ApiResponse<List<PurchaseResponse>> apiResponse = new ApiResponse<>(
                OK,null,this.productService.purchase(purchaseRequestList)
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Thu hồi số lượng sản phẩm đã mua (Dùng tại order khi quy trình đặt hàng không thành công)
     *
     * @param purchaseRequestList:
     */
    @PutMapping("/cancel")
    public ResponseEntity<?> cancelPurchase(
            @RequestBody List<PurchaseRequest> purchaseRequestList
    ) {
        this.productService.cancel(purchaseRequestList);
        return ResponseEntity.ok("Cancel Success");
    }
}
