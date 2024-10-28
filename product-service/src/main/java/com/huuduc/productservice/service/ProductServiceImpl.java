package com.huuduc.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huuduc.productservice.dto.ProductRequest;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.dto.PurchaseRequest;
import com.huuduc.productservice.dto.PurchaseResponse;
import com.huuduc.productservice.exception.ProductPurchaseException;
import com.huuduc.productservice.model.Product;
import com.huuduc.productservice.repository.ProductRepository;
import com.huuduc.productservice.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductRedisService productRedisService;
    private final MessageSource messageSource;

    public ProductResponse create(ProductRequest productRequest) {

        // Ánh xạ (mapping) sản phẩm mới từ yêu cầu productRequest
        Product newProduct = this.productMapper.toProduct(productRequest);

        // Lưu sản phẩm mới vào cơ sở dữ liệu
        this.productRepository.save(newProduct);

        // Logging
        log.info("Product with id {} is saved", newProduct.getId());

        return this.productMapper.fromProduct(newProduct);
    }

    public List<ProductResponse> getAll() throws JsonProcessingException {

        // Lấy tất cả sản phẩm từ cache
        List<Product> productList = this.productRedisService.getAll();
        log.info("Product list successfully retrieved from cache");

        // Kiểm tra nếu cache rỗng thì lấy dữ liệu từ database
        if (productList == null) {

            // Lấy danh sách từ database
            productList = this.productRepository.findAll();
            log.info("Product List Successfully Retrieved from database");

            // Lưu dữ liệu đã lấy vào cache
            productRedisService.saveAllProduct(productList);
        }

        return productList
                .stream()
                .map(this.productMapper::fromProduct)
                .toList();
    }

    public ProductResponse getById(Integer id) {

        // Trả về kết quả nếu tìm thấy ngược lại hiện thị ngoại lệ
        return this.productRepository.findById(id)
                .map(this.productMapper::fromProduct)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                this.messageSource.getMessage("notFound.product", null, LocaleContextHolder.getLocale())
                                        + id
                        )
                );
    }

    @Transactional
    @Override
    public List<PurchaseResponse> purchase(List<PurchaseRequest> purchaseRequestList) {

        //Get the list id product purchase
        var purchaseId = purchaseRequestList
                .stream()
                .map(PurchaseRequest::productId)
                .toList();

        //Get all products, sort it by id
        List<Product> productList = this.productRepository.findAllByIdInOrderById(purchaseId);

        //Check if the products to be purchased exist
        if (productList.size() != purchaseRequestList.size()) {
            throw new EntityNotFoundException(this.messageSource.getMessage("notFound.product", null, LocaleContextHolder.getLocale()));
        }

        //Reorder the list of products to buy from the request
        purchaseRequestList.sort(Comparator.comparing(PurchaseRequest::productId));

        //Reorder the list of products to buy from the request and send to (OrderService)
        List<PurchaseResponse> purchaseProductsResponse = new ArrayList<>();

        //Check the inventory for each product in the request
        for (int i = 0; i < productList.size(); i++) {

            //Get the product in the requirement and the product in the database
            var product = productList.get(i);
            var purchaseRequest = purchaseRequestList.get(i);

            //If the purchase quantity is more than the inventory, an exception is returned
            if (purchaseRequest.quantity() > product.getStock()) {
                throw new ProductPurchaseException(this.messageSource.getMessage(
                        "exception.purchase",
                        null,
                        LocaleContextHolder.getLocale()));
            }

            //Update new stock of this product and save to database
            var newStock = product.getStock() - purchaseRequest.quantity();
            product.setStock(newStock);
            productRepository.save(product);

            //Convert a completed request to a response and add it to the list(purchaseProductsResponse)
            purchaseProductsResponse.add(this.productMapper.toPurchaseResponse(product, purchaseRequest.quantity()));
        }

        return purchaseProductsResponse;
    }

    @Override
    public ProductResponse update(Integer productId, ProductRequest productRequest) {

        //Find product from database
        Product findProduct = this.productRepository.findById(productId)
                .orElseThrow(
                        () -> new EntityNotFoundException(this.messageSource.getMessage(
                                "notFound.product",
                                null,
                                LocaleContextHolder.getLocale()
                        ))
                );

        //Update product from the request
        mergerProduct(findProduct, productRequest);

        this.productRepository.save(findProduct);

        return this.productMapper.fromProduct(findProduct);
    }

    public void mergerProduct(Product product, ProductRequest productRequest) {

        if (!ObjectUtils.isEmpty(productRequest.name())) {
            product.setName(productRequest.name());
        }
        if (!ObjectUtils.isEmpty(productRequest.description())) {
            product.setDescription(productRequest.description());
        }
        if (!ObjectUtils.isEmpty(productRequest.price())) {
            product.setPrice(productRequest.price());
        }

        product.setStock(productRequest.stock());
    }

    @Transactional
    @Override
    public void cancel(List<PurchaseRequest> purchaseRequestList) {

        // Lấy danh sách ID sản phẩm từ yêu cầu hủy
        var purchaseId = purchaseRequestList
                .stream()
                .map(PurchaseRequest::productId)
                .toList();

        // Lấy tất cả sản phẩm từ cơ sở dữ liệu
        List<Product> productList = this.productRepository.findAllByIdInOrderById(purchaseId);

        // Kiểm tra nếu các sản phẩm cần hủy tồn tại
        if (productList.size() != purchaseRequestList.size()) {
            throw new EntityNotFoundException(this.messageSource.getMessage("notFound.product", null, LocaleContextHolder.getLocale()));
        }

        // Sắp xếp lại danh sách các sản phẩm từ yêu cầu hủy
        purchaseRequestList.sort(Comparator.comparing(PurchaseRequest::productId));

        // Cập nhật lại tồn kho cho từng sản phẩm
        for (int i = 0; i < productList.size(); i++) {
            // Lấy sản phẩm trong cơ sở dữ liệu và yêu cầu hủy
            var product = productList.get(i);
            var purchaseRequest = purchaseRequestList.get(i);

            // Cập nhật lại tồn kho của sản phẩm
            var newStock = product.getStock() + purchaseRequest.quantity();
            product.setStock(newStock);
            productRepository.save(product);
        }
    }

}
