package org.tcskart.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tcskart.productservice.bean.Review;
import org.tcskart.productservice.config.JwtUtilValidateToken;
import org.tcskart.productservice.dto.ReviewDto;
import org.tcskart.productservice.exception.FailedOperationException;
import org.tcskart.productservice.service.ReviewService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtUtilValidateToken tokenValidater;

    @Test
    void testAddReviewSuccess() throws Exception {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setDescription("Great product");
        reviewDto.setRating(4.5);

        Mockito.when(reviewService.addReview(any(ReviewDto.class))).thenReturn(true);

        mockMvc.perform(post("/product/review/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Review added successfully"));
    }

    @Test
    void testAddReviewFailure() throws Exception {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setProductId(1L);
        reviewDto.setDescription("Bad product");
        reviewDto.setRating(2.0);

        Mockito.when(reviewService.addReview(any(ReviewDto.class))).thenReturn(false);

        mockMvc.perform(post("/product/review/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Sorry, We couldn't add review"));
    }

    @Test
    void testFetchProductReviewsSuccess() throws Exception {
        Review review = new Review();
        review.setDescription("Excellent");
        review.setRating(5.0);

        Mockito.when(reviewService.getProductReviews(1L)).thenReturn(List.of(review));

        mockMvc.perform(get("/product/review")
                .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Excellent"));
    }

    @Test
    void testFetchProductReviewsFailure() throws Exception {
        Mockito.when(reviewService.getProductReviews(1L)).thenReturn(null);

        mockMvc.perform(get("/product/review")
                .param("productId", "1"))
                .andExpect(status().isInternalServerError());
    }
}
