package org.tcskart.order.feinclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.tcskart.order.dto.CartDTO;
import org.tcskart.order.dto.OrderProduct;

@FeignClient(name = "api-gateway-microservice")
public interface ProductClient {
	@PostMapping("products/decrease")
    void decreaseProducts(@RequestBody List<OrderProduct> orderedItems);
	
	@PostMapping("carts/{userId}/placeorder")
    List<CartDTO> getCartItemsByUserId(@PathVariable("userId") Long userId);
}
