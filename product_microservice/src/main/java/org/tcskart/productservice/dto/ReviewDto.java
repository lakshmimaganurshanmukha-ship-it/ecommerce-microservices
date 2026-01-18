package org.tcskart.productservice.dto;

import lombok.Data;

@Data
public class ReviewDto {
     private Long productId;
     private String description;
     private Double rating;
}
