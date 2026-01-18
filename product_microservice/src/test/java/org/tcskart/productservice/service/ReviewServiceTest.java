package org.tcskart.productservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.tcskart.productservice.bean.Product;
import org.tcskart.productservice.bean.Review;
import org.tcskart.productservice.dto.ReviewDto;
import org.tcskart.productservice.exception.FailedOperationException;
import org.tcskart.productservice.exception.ProductNotFoundException;
import org.tcskart.productservice.repository.ProductRepository;
import org.tcskart.productservice.repository.ReviewRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReview_Success() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setDescription("Great product");
        reviewDto.setRating(4.5);

        Product product = new Product();
        product.setId(1L);

        Review savedReview = new Review(product, reviewDto.getDescription(), reviewDto.getRating());

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepo.save(any(Review.class))).thenReturn(savedReview);

        Boolean result = reviewService.addReview(reviewDto);

        assertTrue(result);
        verify(reviewRepo, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReview_ProductNotFound() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(99L);
        reviewDto.setDescription("Bad product");
        reviewDto.setRating(2.0);

        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> reviewService.addReview(reviewDto));

        verify(reviewRepo, never()).save(any(Review.class));
    }

    @Test
    void testAddReview_SaveFails() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setDescription("Average");
        reviewDto.setRating(3.0);

        Product product = new Product();
        product.setId(1L);

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepo.save(any(Review.class))).thenReturn(null);

        assertThrows(FailedOperationException.class, () -> reviewService.addReview(reviewDto));
    }

    @Test
    void testGetProductReviews_ReturnsList() {
        Long productId = 1L;
        List<Review> mockReviews = Arrays.asList(
                new Review(new Product(), "Nice", 4.0),
                new Review(new Product(), "Good", 3.5)
        );

        when(reviewRepo.findByProductId(productId)).thenReturn(mockReviews);

        List<Review> result = reviewService.getProductReviews(productId);

        assertEquals(2, result.size());
        verify(reviewRepo, times(1)).findByProductId(productId);
    }
}

