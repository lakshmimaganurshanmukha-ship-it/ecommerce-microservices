package org.tcskart.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.config.JwtUtilValidateToken;
import org.tcskart.productservice.dto.OrderProduct;
import org.tcskart.productservice.dto.ProductRequestDTO;
import org.tcskart.productservice.dto.ProductResponseDTO;
import org.tcskart.productservice.service.ProductService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtUtilValidateToken tokenValidater;


    @Test
    void testAddProduct() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Test Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.png"), List.of("123456"));
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Test Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.png"));

        Mockito.when(productService.addProduct(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Test", "desc", BigDecimal.valueOf(100), 5, "Books", List.of("img.jpg"));
        Mockito.when(productService.getAllProducts(0, 10, null, null)).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Test", "desc", BigDecimal.valueOf(100), 5, "Books", List.of("img.jpg"));
        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully."));
    }
    
    @Test
    void testGetProductByName() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Test Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.png"));
        Mockito.when(productService.getProductByName("Test Product")).thenReturn(product);

        mockMvc.perform(get("/products/search")
                .param("productName", "Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }
    
    
    @Test
    void testGetAllProductsNoPagination() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Test Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.png"));
        Mockito.when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
    
    @Test
    void testUpdateProduct() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Updated Product", "desc", BigDecimal.valueOf(150), 5, "Electronics", List.of("image.png"), List.of("123456"));
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Updated Product", "desc", BigDecimal.valueOf(150), 5, "Electronics", List.of("image.png"));

        Mockito.when(productService.updateProduct(Mockito.eq(1L), any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

  
    @Test
    void testDecreaseProductsCount() throws Exception {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProductId(1L);
        orderProduct.setQuantity(2);
        orderProduct.setAmount(200);

        mockMvc.perform(post("/products/decrease")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(orderProduct))))
                .andExpect(status().isOk());

        Mockito.verify(productService).decreaseProductsCount(any());
    }


    @Test
    void testGetRestockProducts() throws Exception {
        Product product = new Product();  // You can populate this if needed
        Mockito.when(productService.getRestockProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products/restock"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testCheckAvailability_True() throws Exception {
        Mockito.when(productService.isProductAvailable(1L, "123456")).thenReturn(true);

        mockMvc.perform(get("/products/1/availability")
                .param("pincode", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product is available in your area."));
    }

    @Test
    void testCheckAvailability_False() throws Exception {
        Mockito.when(productService.isProductAvailable(1L, "999999")).thenReturn(false);

        mockMvc.perform(get("/products/1/availability")
                .param("pincode", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sorry, delivery is not available in your pincode."));
    }


}

