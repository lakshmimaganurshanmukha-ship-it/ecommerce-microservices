package org.tcskart.productservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.bean.ProductAvailability;
import org.tcskart.productservice.bean.Restock;
import org.tcskart.productservice.dto.OrderProduct;
import org.tcskart.productservice.dto.ProductQuantity;
import org.tcskart.productservice.dto.ProductRequestDTO;
import org.tcskart.productservice.dto.ProductResponseDTO;
import org.tcskart.productservice.exception.DuplicateProductException;
import org.tcskart.productservice.exception.InvalidImageFormatException;
import org.tcskart.productservice.exception.ProductNotFoundException;
import org.tcskart.productservice.repository.ProductAvailabilityRepository;
import org.tcskart.productservice.repository.ProductRepository;
import org.tcskart.productservice.repository.RestockRepository;

@Service
public class ProductService implements ProductServiceInterface {

	private ProductRepository productRepository;

	private EmailService emailService;

	private RestockRepository restockRepository;

	private ProductAvailabilityRepository availabilityRepo;

	public ProductService(ProductRepository productRepository, EmailService emailService,
			RestockRepository restockRepository, ProductAvailabilityRepository productAvailabilityRepository) {
		this.productRepository = productRepository;
		this.emailService = emailService;
		this.restockRepository = restockRepository;
		this.availabilityRepo = productAvailabilityRepository;
	}

	@Override
	public ProductResponseDTO addProduct(ProductRequestDTO dto) {
		Optional<Product> existing = productRepository.findByName(dto.getName());
		if (existing.isPresent()) {
			throw new DuplicateProductException("Product with name '" + dto.getName() + "' already exists.");
		}
		if (dto.getImageUrl() != null) {
			for (String url : dto.getImageUrl()) {
				if (!url.toLowerCase().endsWith(".jpg") && !url.toLowerCase().endsWith(".png")) {
					throw new InvalidImageFormatException("Only JPG and PNG image formats are allowed: " + url);
				}
			}
		}

		Product product = new Product();
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setQuantity(dto.getQuantity());
		product.setCategory(dto.getCategory());
		product.setImageUrls(dto.getImageUrl());

		Product saved = productRepository.save(product);

		if (dto.getPincodes() != null) {
			for (String pin : dto.getPincodes()) {
				ProductAvailability availability = new ProductAvailability();
				availability.setProductId(saved.getId());
				availability.setPincode(pin);
				availabilityRepo.save(availability);
			}
		}

		return mapToResponse(saved);
	}

	@Override
	public List<ProductResponseDTO> getAllProducts(int page, int size, String category, String search) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Product> productPage;

		if (category != null && search != null) {
			productPage = productRepository
					.findByCategoryIgnoreCaseAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(category,
							search, search, pageable);
		} else if (category != null) {
			productPage = productRepository.findByCategoryIgnoreCase(category, pageable);
		} else if (search != null) {
			productPage = productRepository.searchByCategoryORKeyword(search, search, pageable);
		} else {
			productPage = productRepository.findAll(pageable);
		}

		return productPage.getContent().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	private ProductResponseDTO mapToResponseDTO(Product product) {
		return ProductResponseDTO.builder().id(product.getId()).name(product.getName())
				.description(product.getDescription()).price(product.getPrice()).category(product.getCategory())
				.quantity(product.getQuantity()).imageUrl(product.getImageUrls()).build();
	}

	@Override
	public ProductResponseDTO getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		return mapToResponse(product);
	}

	private ProductResponseDTO mapToResponse(Product product) {
		return new ProductResponseDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getQuantity(), product.getCategory(), product.getImageUrls());
	}

	@Override
	public List<ProductResponseDTO> getAllProducts() {
		List<Product> products = productRepository.findAll();
		return products.stream().map(this::mapToResponseDTO).toList();
	}

	@Override
	public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
		Product existing = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
		// ✅ Validate image URLs
		if (dto.getImageUrl() != null) {
			for (String url : dto.getImageUrl()) {
				if (!url.toLowerCase().endsWith(".jpg") && !url.toLowerCase().endsWith(".png")) {
					throw new InvalidImageFormatException("Only JPG and PNG image formats are allowed: " + url);
				}
			}
		}
		// Update fields
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setPrice(dto.getPrice());
		existing.setCategory(dto.getCategory());
		Integer existingQuantity = existing.getQuantity();
		Integer updatedQuantity = existingQuantity + dto.getQuantity();
		existing.setQuantity(updatedQuantity);
		existing.setImageUrls(dto.getImageUrl());

		Product updated = productRepository.save(existing);
		restockProduct(updated);
		return mapToResponseDTO(updated);
	}

	public ProductResponseDTO getProductByName(String productName) {
		// TODO Auto-generated method stub
		Product product = productRepository.findByName(productName)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with Name:" + productName));

		return mapToResponse(product);
	}

	public ProductResponseDTO updateProductQuantity(String productName, Integer quantity) {
		Product product = productRepository.findByName(productName)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with Name:" + productName));
		Integer existingQuantity = product.getQuantity();
		Integer updatedQuantity = existingQuantity + quantity;
		product.setQuantity(updatedQuantity);
		Product updated = productRepository.save(product);
		return mapToResponse(product);
	}

	@Transactional
	public void decreaseProductsCount(List<OrderProduct> orderedItems) {
		for (OrderProduct orderProduct : orderedItems) {
			Product product = productRepository.getById(orderProduct.getProductId());
			product.setQuantity(product.getQuantity() - orderProduct.getQuantity());

			this.restockProduct(productRepository.save(product));

		}
	}

	@Transactional
	public void restockProduct(Product product) {

		if (product.getQuantity() < 6) {
			Restock restock = restockRepository.findByProduct(product);
			if (restock != null) {
				restockRepository.deleteByProduct(product);
			}

			Restock newRestock = new Restock();
			newRestock.setProduct(product);
			restockRepository.save(newRestock);

		} else {
			if (restockRepository.findByProduct(product) != null) {
				restockRepository.deleteByProduct(product);
			}
		}
		long restockCount = restockRepository.count();
		if (restockCount == 1 && restockCount != 0) {
			emailService.sendEmail("nn6334920@gmail.com", "hello",
					"Please restock the products, no of products to be restock" + restockCount);
		}
	}

	public List<Product> getRestockProducts() {

		return restockRepository.findAllRestockProducts();

	}

	@Transactional
	public void updateProduct(ProductQuantity productQuantity, Long productId) {
		Product product = productRepository.getById(productId);
		System.out.println(product);
		product.setQuantity(product.getQuantity() + productQuantity.getQuantity());
		this.restockProduct(productRepository.save(product));
	}

	@Override
	public void deleteProduct(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
		productRepository.delete(product);
	}

	public boolean isProductAvailable(Long productId, String pincode) {
		return availabilityRepo.existsByProductIdAndPincode(productId, pincode);
	}

}
