package org.tcskart.order.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tcskart.order.beans.Order;
import org.tcskart.order.beans.OrderItem;
import org.tcskart.order.dto.CartDTO;
import org.tcskart.order.dto.CartItemDTO;
import org.tcskart.order.dto.OrderProduct;
import org.tcskart.order.dto.Statistics;
import org.tcskart.order.dto.Status;
import org.tcskart.order.dto.TopProduct;
import org.tcskart.order.feinclient.ProductClient;
import org.tcskart.order.repository.OrderItemRepository;
import org.tcskart.order.repository.OrderRepository;


@Service
public class OrderServiceImplimentation implements OrderService{
	
	@Autowired
	private OrderRepository repo;
	@Autowired
	private OrderItemRepository repoItem;
	
	@Autowired
	EmailService eservice;

//	@Autowired
//	private CartClient  cartClient;
	
	@Autowired
	ProductClient productClient;

	@Override
	public List<OrderItem> getAllOrdersByUserId(Long userId) {
		List<OrderItem> orderItems=repoItem.findall(userId);
		return orderItems;
		
	}
	
	public String updateStatus(int orderId,Status status) {
		Order order=repo.findById(orderId).get();
		order.setOrderstatus(status.getStatus());
		Order newOrder=repo.save(order);
	if(newOrder!=null) {
		return newOrder.getOrderstatus();
	}
	return null;
		
		
		
		
	}
	

	
	@Override
	public List<String> trackOrderStatus(Long userId) {
	    List<Order> userOrders = repo.findByUserId(userId);
	    List<String> statusList = new ArrayList<>();

	    for (Order order : userOrders) {
	        LocalDateTime orderTime = order.getOrderDate();
	        LocalDateTime now = LocalDateTime.now();
	        Duration duration = Duration.between(orderTime, now);
	        long minutesPassed = duration.toMinutes();

	        String dynamicStatus;

	        if (minutesPassed < 2) {
	            dynamicStatus = "PENDING";
	        } else if (minutesPassed < 5) {
	            dynamicStatus = "SHIPPED";
	        } else {
	            dynamicStatus = "DELIVERED";
	        }

	        String status = "Order ID: " + order.getOrderId() +
	                        ", Status: " + order.getOrderstatus() +
	                        ", Date: " + orderTime +
	                        ", Total Amount: ₹" + order.getTotalAmount();
	        statusList.add(status);
	    }

	    return statusList;
	}


	@Override
	public Order placeOrder(Long userId) {
		// 1. Fetch cart items from cart service using Feign
		List<CartDTO> cartItemList = productClient.getCartItemsByUserId(userId);
        System.out.println(cartItemList.size());
		if (cartItemList == null || cartItemList.isEmpty()) {
			throw new RuntimeException("Cart is empty for user ID: " + userId);
		}

		// 2. Create a new order
		Order order = new Order();
		order.setUserId(userId);
		order.setOrderstatus("PLACED");
		order.setPaymentMode("CashOnDelivery");
		order.setOrderDate(LocalDateTime.now());
		order.setTotalAmount(0L); // initially 0
		Order savedOrder = repo.save(order);

		// 3. Add all items and calculate total
		long totalAmount = 0L;
		 List<OrderProduct> productList = new ArrayList<>();

		for (CartDTO cartItemDTO : cartItemList) {
			OrderItem item = new OrderItem();
			item.setProductId(cartItemDTO.getProductId());
			item.setQuantity(cartItemDTO.getQuantity());
			item.setAmount(cartItemDTO.getTotalPrice());
			item.setOrder(savedOrder);

			totalAmount += cartItemDTO.getTotalPrice();
			repoItem.save(item);
			 productList.add(new OrderProduct(
			            cartItemDTO.getProductId(),
			            cartItemDTO.getQuantity()
			        ));
			
		}

		// 4. Update order with total amount
		savedOrder.setTotalAmount(totalAmount);
		
		Order newOrder= repo.save(savedOrder);
		productClient.decreaseProducts(productList);
		return newOrder;
	}


	public Statistics statstics(){
        int totalOrders=repo.findAll().size();
        int totalSales =repoItem.findAll().size();
        List<TopProduct> topProducts=repoItem.findTopProductsRaw();
        int avrageUsers=repo.getAverageOrdersPerDay(); // no of orders per day
        int totalRegisteredUsers=100;
        int usersPlacedOrder=repo.getTotalOrders();
        double userEngagement = ((double) usersPlacedOrder / totalRegisteredUsers)*100; //
        Statistics statistics=new Statistics();
        statistics.setUserEngagement(userEngagement);
        statistics.setAvrageUsers(avrageUsers);
        statistics.setTotalOrders(totalOrders);
        statistics.setTotalSales(totalSales);
        statistics.setTopProducts(topProducts);
return statistics;


}


}

