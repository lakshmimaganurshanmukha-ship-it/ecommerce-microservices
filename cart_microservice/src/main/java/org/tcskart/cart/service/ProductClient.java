package org.tcskart.cart.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.tcskart.cart.dto.ProductResponseDTO;

@FeignClient(name = "api-gateway-microservice")  // If using Eureka
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponseDTO getProductById(@PathVariable("id") Long productId);
}
