package org.tcskart.order.controller;

import java.util.List;

//import org.eclipse.angus.mail.imap.protocol.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tcskart.order.beans.Order;
import org.tcskart.order.beans.OrderItem;
import org.tcskart.order.config.JwtUtilValidateToken;
import org.tcskart.order.dto.Statistics;
import org.tcskart.order.dto.Status;
import org.tcskart.order.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/orders")
public class OrderController {
	
	
//	 @Autowired
//	    private OrderService orderService;
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private JwtUtilValidateToken jwtUtilValidateToken;
	   
	        @PostMapping("/place")
	        public void placeOrder(HttpServletRequest request) {
			String token=request.getHeader("Authorization").substring(7);
			Long userId=jwtUtilValidateToken.getClaimId(token);

				orderService.placeOrder(userId);
	        }
	        

	        @GetMapping("/all")
	        public List<OrderItem> getOrdersByUserId(HttpServletRequest request) {
	        	String token=request.getHeader("Authorization").substring(7);
				Long userId=jwtUtilValidateToken.getClaimId(token);
	            return orderService.getAllOrdersByUserId(userId); 
	        }
	        
	        
	        @GetMapping("/status")
	        public List<String> getOrderStatusByUser(HttpServletRequest request) {
	        	String token=request.getHeader("Authorization").substring(7);
				Long userId=jwtUtilValidateToken.getClaimId(token);
	            return orderService.trackOrderStatus(userId);
	        }

	        	@PreAuthorize("hasRole('ADMIN')")
	        @GetMapping("/stats")
            public Statistics statistics() {
return orderService.statstics();
            }
	        
//	        	@PreAuthorize("hasRole('ADMIN')")
	        @PostMapping("/update/status/{orderId}")
	        public String updateStatus(@PathVariable int orderId,@RequestBody Status status) {
	        	return orderService.updateStatus(orderId,status);
	        	
	        }

}
