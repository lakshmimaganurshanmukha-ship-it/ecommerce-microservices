package org.tcskart.order;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tcskart.order.beans.OrderItem;
import org.tcskart.order.config.JwtUtilValidateToken;
//import org.tcskart.order.controller.OrderController;
import org.tcskart.order.controller.OrderController;
import org.tcskart.order.service.OrderService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {


	@TestConfiguration
	static class MockConfig {
		@Bean
		public OrderService orderService() {
			return Mockito.mock(OrderService.class);
		}

		@Bean
		public JwtUtilValidateToken jwtUtil() {
			return Mockito.mock(JwtUtilValidateToken.class);
		}
	}
	@Autowired
	private MockMvc mockMvc;



	@Autowired
	private OrderService orderService;

	@Autowired
	private JwtUtilValidateToken jwtUtil;



	private final String fakeToken = "fake.jwt.token";
	private final String authHeader = "Bearer " + fakeToken;
	private final Long userId = 1L;

	@Test
	void testPlaceOrder() throws Exception {
		when(jwtUtil.getClaimId(fakeToken)).thenReturn(userId);

		mockMvc.perform(post("/orders/place")
						.header("Authorization", authHeader))
				.andExpect(status().isOk());

		verify(orderService).placeOrder(userId);
	}

	@Test
	void testGetOrdersByUserId() throws Exception {
		List<OrderItem> mockOrders = List.of(new OrderItem());
		when(jwtUtil.getClaimId(fakeToken)).thenReturn(userId);
		when(orderService.getAllOrdersByUserId(userId)).thenReturn(mockOrders);

		mockMvc.perform(get("/orders/all")
						.header("Authorization", authHeader)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));

		verify(orderService).getAllOrdersByUserId(userId);
	}

	@Test
	void testGetOrderStatusByUser() throws Exception {
		List<String> statuses = List.of("Placed", "Shipped");
		when(jwtUtil.getClaimId(fakeToken)).thenReturn(userId);
		when(orderService.trackOrderStatus(userId)).thenReturn(statuses);

		mockMvc.perform(get("/orders/status")
						.header("Authorization", authHeader)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));

		verify(orderService).trackOrderStatus(userId);
	}
}
