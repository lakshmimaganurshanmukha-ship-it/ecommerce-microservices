package org.tcskart.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.dto.OrderProduct;
import org.tcskart.productservice.dto.ProductQuantity;
import org.tcskart.productservice.dto.ProductRequestDTO;
import org.tcskart.productservice.dto.ProductResponseDTO;
import org.tcskart.productservice.service.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

	private ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/products")
	public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody @Valid ProductRequestDTO requestDTO) {
		ProductResponseDTO responseDTO = productService.addProduct(requestDTO);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductResponseDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "15") int size, @RequestParam(required = false) String category,
			@RequestParam(required = false) String search) {

		List<ProductResponseDTO> products = productService.getAllProducts(page, size, category, search);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	@GetMapping("/products/search")
	public ResponseEntity<ProductResponseDTO> getProductByName(@RequestParam String productName) {
		return ResponseEntity.ok(productService.getProductByName(productName));
	}

	@GetMapping("/products/all")
	public List<ProductResponseDTO> getAllProducts() {
		return productService.getAllProducts();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/products/update/{id}")
	public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
			@RequestBody ProductRequestDTO product) {
		ProductResponseDTO products = productService.updateProduct(id, product);
		return ResponseEntity.ok(products);
	}

	@PostMapping("/products/decrease")
	public void decreaseProductsCount(@RequestBody List<OrderProduct> orderedItems) {
		productService.decreaseProductsCount(orderedItems);

	}

	@GetMapping("/products/restock")
	public List<Product> getRestockProducts() {
		return productService.getRestockProducts();

	}

	@PostMapping("/products/update/{productId}")
	public void updateProduct(@PathVariable Long productId, @RequestBody ProductQuantity productQuantity) {
		productService.updateProduct(productQuantity, productId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/products/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.ok("Product deleted successfully.");
	}

	@GetMapping("/products/{productId}/availability")
	public ResponseEntity<String> checkAvailability(@PathVariable Long productId, @RequestParam String pincode) {

		boolean available = productService.isProductAvailable(productId, pincode);

		if (available) {
			return ResponseEntity.ok("Product is available in your area.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Sorry, delivery is not available in your pincode.");
		}
	}

}
