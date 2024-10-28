package com.huuduc.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuduc.productservice.dto.ProductRequest;
import com.huuduc.productservice.dto.ProductResponse;
import com.huuduc.productservice.repository.ProductRepository;
import com.huuduc.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@SuppressWarnings("unused")
class ProductServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductService productService;


    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    public void shouldCreateProduct() throws Exception {

        ProductRequest productRequest = getProduct();
        String productRequestString = this.objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
    }

    private ProductRequest getProduct() {
        return new ProductRequest(
                "a", "a", 13, BigDecimal.valueOf(123)
        );
    }

    @Test
    public void getAllProductSuccess() throws Exception {

        shouldCreateProduct();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").isNotEmpty());
    }

    @Test
    public void getByIdSuccess() throws Exception {

        Integer id = 1;
        ProductResponse productResponse = new ProductResponse(id, "a", "a", BigDecimal.valueOf(25), 1, LocalDateTime.MAX, LocalDateTime.MIN);

        shouldCreateProduct();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("a"));
    }

    @Test
    public void updateProductSuccess() throws Exception {

        shouldCreateProduct();
        ProductRequest productRequest = new ProductRequest("b", "b", 25, BigDecimal.valueOf(30));

        String productRequestString = this.objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("b"))
                .andExpect(jsonPath("$.description").value("b"))
                .andExpect(jsonPath("$.price").value(30))
                .andExpect(jsonPath("$.stock").value(25));
    }
}
