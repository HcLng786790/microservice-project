package com.huuduc.productservice.mapper;

import com.huuduc.productservice.dto.ProductRequest;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.dto.PurchaseResponse;
import com.huuduc.productservice.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse fromProduct(Product product){

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedDate(),
                product.getLastModifiedDate()
        );
    }

    public Product toProduct(ProductRequest productRequest){

        return Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .stock(productRequest.stock())
                .build();
    }

    public PurchaseResponse toPurchaseResponse(Product product,int quantity){

        return new PurchaseResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }
}
