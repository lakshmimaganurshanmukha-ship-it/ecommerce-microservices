package org.tcskart.productservice.service;

import java.util.List;

import org.tcskart.productservice.dto.ProductRequestDTO;
import org.tcskart.productservice.dto.ProductResponseDTO;


public interface ProductServiceInterface {
    ProductResponseDTO addProduct(ProductRequestDTO requestDTO);
    List<ProductResponseDTO> getAllProducts(int page, int size, String category, String search);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);
	void deleteProduct(Long id);
}
