package org.tcskart.productservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.bean.Review;
import org.tcskart.productservice.dto.ReviewDto;
import org.tcskart.productservice.exception.FailedOperationException;
import org.tcskart.productservice.exception.ProductNotFoundException;
import org.tcskart.productservice.repository.ProductRepository;
import org.tcskart.productservice.repository.ReviewRepository;

@Service
public class ReviewService {
	
	private ReviewRepository reviewRepo;
	private ProductRepository productRepo;
    
	public ReviewService(ReviewRepository reviewRepo, ProductRepository productRepo) {
		this.reviewRepo = reviewRepo;
		this.productRepo = productRepo;
	}
	
	public Boolean addReview(ReviewDto review) {
		Optional<Product> product=productRepo.findById(review.getProductId());
		if(product.isEmpty()) throw new ProductNotFoundException("Product not found for which you want to add reveiw");
		
		Review productReview=new Review(product.get(),review.getDescription(),review.getRating());
		Review operationStatus=(Review)reviewRepo.save(productReview);
		if(operationStatus==null) {
			throw new FailedOperationException("Couldn't add review");
		}
		return true;
	}
	
	public List<Review> getProductReviews(Long productId){
		 List<Review> reviews=reviewRepo.findByProductId(productId);
		 return reviews;
	}
}
