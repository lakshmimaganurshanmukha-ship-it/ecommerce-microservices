package org.tcskart.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.tcskart.productservice.bean.Review;
import org.tcskart.productservice.dto.ReviewDto;
import org.tcskart.productservice.service.ReviewService;
import org.tcskart.productservice.exception.FailedOperationException;

@RestController
@RequestMapping("/product/review")
public class ReviewController {
      
    private ReviewService service;
    
    public ReviewController(ReviewService service) {
    	this.service=service;
    }
    
    @PostMapping("/add")
    public ResponseEntity<Object> addReview(@RequestBody ReviewDto review){
    	Boolean status=service.addReview(review);
    	if(status) {
    		return new ResponseEntity<Object>("Review added successfully",HttpStatus.OK);
    	}
    	return new ResponseEntity<Object>("Sorry, We couldn't add review",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @GetMapping
    public ResponseEntity<Object> fetchProductReviews(@RequestParam Long productId){
    	List<Review> reviewList=service.getProductReviews(productId);
    	if(reviewList==null)
    		throw new FailedOperationException("Unable to fetch reviews");
    	return new ResponseEntity<Object>(reviewList,HttpStatus.OK);
    }
}
