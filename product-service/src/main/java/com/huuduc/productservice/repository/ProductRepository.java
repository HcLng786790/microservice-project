package com.huuduc.productservice.repository;

import com.huuduc.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

    List<Product> findAllByIdInOrderById(List<Integer> purchaseId);
}
