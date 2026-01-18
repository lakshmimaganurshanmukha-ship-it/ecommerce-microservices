package org.tcskart.productservice.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.eq;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.bean.ProductAvailability;
import org.tcskart.productservice.dto.ProductRequestDTO;
import org.tcskart.productservice.repository.ProductAvailabilityRepository;
import org.tcskart.productservice.repository.ProductRepository;
import org.tcskart.productservice.repository.RestockRepository;
import org.tcskart.productservice.dto.ProductResponseDTO;
import org.tcskart.productservice.exception.DuplicateProductException;
import org.tcskart.productservice.exception.InvalidImageFormatException;
import org.tcskart.productservice.repository.ProductRepository;
import org.tcskart.productservice.repository.RestockRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private RestockRepository restockRepository;

    @Mock
    private ProductAvailabilityRepository availabilityRepository;

    @InjectMocks
    private ProductService productService;
    
    
    @Test
    void testAddProduct_Success() {
        ProductRequestDTO dto = new ProductRequestDTO(
            "Test Product", "Description", BigDecimal.valueOf(99.99), 10,
            "Category", List.of("img.jpg"), List.of("123456")
        );

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setDescription("Description");
        savedProduct.setPrice(BigDecimal.valueOf(99.99));
        savedProduct.setQuantity(10);
        savedProduct.setCategory("Category");
        savedProduct.setImageUrls(List.of("img.jpg"));

        when(productRepository.findByName("Test Product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDTO response = productService.addProduct(dto);

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        verify(availabilityRepository).save(any(ProductAvailability.class));
    }
    
    @Test
    void testGetProductById_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductById(1L);

        assertEquals("Test", response.getName());
    }

    
    @Test
    void testUpdateProduct_Success() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setQuantity(5);

        ProductRequestDTO dto = new ProductRequestDTO("Updated", "Updated desc",
            BigDecimal.valueOf(100), 5, "Updated Cat", List.of("img.jpg"), null);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(productRepository.save(any())).thenReturn(existing);

        ProductResponseDTO response = productService.updateProduct(1L, dto);

        assertEquals("Updated", response.getName());
    }

    
    @Test
    void testDeleteProduct_Success() {
        Product product = new Product();
        product.setId(1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void testGetRestockProducts() {
        List<Product> mockList = List.of(new Product(), new Product());
        Mockito.when(restockRepository.findAllRestockProducts()).thenReturn(mockList);

        List<Product> result = productService.getRestockProducts();

        assertEquals(2, result.size());
    }

    @Test
    void testAddProduct_ThrowsDuplicateProductException() {
        // Arrange
        ProductRequestDTO dto = new ProductRequestDTO("Test Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.jpg"), List.of("123456"));
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(new Product()));

        // Act & Assert
        assertThrows(DuplicateProductException.class, () -> productService.addProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProduct_ThrowsInvalidImageFormatException() {
        // Arrange
        ProductRequestDTO dto = new ProductRequestDTO("New Product", "desc", BigDecimal.valueOf(100), 10, "Electronics", List.of("image.bmp"), List.of("123456"));
        when(productRepository.findByName("New Product")).thenReturn(Optional.empty());

        // Act & Assert
        InvalidImageFormatException thrown = assertThrows(InvalidImageFormatException.class, () -> productService.addProduct(dto));
        assertTrue(thrown.getMessage().contains("Only JPG and PNG"));
        verify(productRepository, never()).save(any());
    }
    
    private Product sampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);
        product.setCategory("Electronics");
        return product;
    }

    @Test
    void testGetAllProducts_WithCategoryAndSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = sampleProduct();
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq("Electronics"), eq("Test"), eq("Test"), eq(pageable))).thenReturn(page);

        List<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Electronics", "Test");

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testGetAllProducts_WithCategoryOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = sampleProduct();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findByCategoryIgnoreCase(eq("Electronics"), eq(pageable))).thenReturn(page);

        List<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Electronics", null);

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testGetAllProducts_WithSearchOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = sampleProduct();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.searchByCategoryORKeyword(eq("Test"), eq("Test"), eq(pageable))).thenReturn(page);

        List<ProductResponseDTO> result = productService.getAllProducts(0, 10, null, "Test");

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testGetAllProducts_WithNoFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = sampleProduct();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(eq(pageable))).thenReturn(page);

        List<ProductResponseDTO> result = productService.getAllProducts(0, 10, null, null);

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
    }

}
