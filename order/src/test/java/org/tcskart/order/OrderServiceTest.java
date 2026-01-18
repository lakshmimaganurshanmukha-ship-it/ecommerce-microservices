package org.tcskart.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tcskart.order.beans.Order;
import org.tcskart.order.beans.OrderItem;
import org.tcskart.order.dto.CartDTO;
import org.tcskart.order.feinclient.ProductClient;
import org.tcskart.order.repository.OrderItemRepository;
import org.tcskart.order.repository.OrderRepository;
import org.tcskart.order.service.EmailService;
import org.tcskart.order.service.OrderServiceImplimentation;

class OrderServiceTest {

    @InjectMocks
    private OrderServiceImplimentation orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductClient cartClient;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrdersByUserId() {
        Long userId = 1L;
        List<OrderItem> mockItems = List.of(new OrderItem());
        when(orderItemRepository.findall(userId)).thenReturn(mockItems);

        List<OrderItem> result = orderService.getAllOrdersByUserId(userId);

        assertEquals(1, result.size());
        verify(orderItemRepository).findall(userId);
    }

    @Test
    void testTrackOrderStatus() {
        Long userId = 1L;
        Order order = new Order();
        order.setOrderId(101);
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now().minusMinutes(3));
        order.setTotalAmount(500L);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        List<String> statuses = orderService.trackOrderStatus(userId);

        assertEquals(1, statuses.size());
        assertTrue(statuses.get(0).contains("SHIPPED"));
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void testPlaceOrder_Success() {
        Long userId = 1L;
        CartDTO cartItem = new CartDTO();
        cartItem.setProductId(101L);
        cartItem.setTotalPrice(1L);
        cartItem.setQuantity(2);

        when(cartClient.getCartItemsByUserId(userId)).thenReturn(List.of(cartItem));

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderstatus("PLACED");
        order.setPaymentMode("CashOnDelivery");
        order.setTotalAmount(0L);

        Order savedOrder = new Order();
        savedOrder.setOrderId(500);
        savedOrder.setUserId(userId);
        savedOrder.setOrderDate(order.getOrderDate());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.placeOrder(userId);

        assertNotNull(result);
        verify(cartClient).getCartItemsByUserId(userId);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(orderRepository, times(2)).save(any(Order.class)); // before & after setting total
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        Long userId = 1L;
        when(cartClient.getCartItemsByUserId(userId)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(userId);
        });

        assertEquals("Cart is empty for user ID: " + userId, exception.getMessage());
    }
}
