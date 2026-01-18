package org.tcskart.productservice.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class ReviewResponseDTO {
    private String userId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
