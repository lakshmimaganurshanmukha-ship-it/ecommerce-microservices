package org.tcskart.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopProduct {
    Long productId;
    BigDecimal percentage;
 

    public TopProduct(Long productId,  BigDecimal percentage) {
        this.productId = productId;
        this.percentage = percentage;
    
    }
}
