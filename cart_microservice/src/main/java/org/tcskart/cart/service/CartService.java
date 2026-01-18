package org.tcskart.cart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartDTO;
import org.tcskart.cart.bean.CartItem;
import org.tcskart.cart.bean.CartWishlist;
import org.tcskart.cart.dao.CartItemRepository;
import org.tcskart.cart.dao.CartRepository;
import org.tcskart.cart.dao.WishlistRepository;
import org.tcskart.cart.dto.ProductResponseDTO;
import org.tcskart.cart.exceptions.ProductAlreadyInCartException;
import org.tcskart.cart.exceptions.ProductAlreadyInWishListException;
import org.tcskart.cart.exceptions.ProductNotInCartException;
import org.tcskart.cart.exceptions.ProductQuantityException;
import org.tcskart.cart.exceptions.UserNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class CartService {
	
	
	@Autowired
	private ProductClient productClient;
	
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired 
	private WishlistRepository wishListRepo;
	
	
	
	//UTILITY METHOD TO CREATE CART FOR A USER
	
	public Cart createCart(Long userId) {    //creates a new cart
		Cart newuser = new Cart();
		newuser.setUserId(userId);
		return cartRepo.save(newuser);
		
	}
	
	//TO GET THE CART
	
	public Optional<Cart> getUserCart(Long userId) {
		if(cartRepo.findByUserId(userId).isPresent()) {
			return cartRepo.findByUserId(userId);
		}else {
			throw new UserNotFoundException();
		}
	}
	
	//TO GET CART ITEMS
	
	public List<CartItem> getCartItems(Long userId){
		return cartItemRepo.findByCart_UserId(userId);
	}
	
	
	//ADD TO CART OR UPDATE THE QUANTITY OF THE ITEM IN CART
	
	public String addToCart(Long userId,CartItem item){        //Update quantity or add item with quantity
  	    System.out.println(item.getPrice());
		int quantity = item.getQuantity();		
		Cart cart;
		
		ProductResponseDTO product = productClient.getProductById(item.getProductId());
		
		if(cartRepo.findByUserId(userId).isPresent()) {
			cart = cartRepo.findByUserId(userId).get();
		}else {
			cart = createCart(userId);
		}
		
		
		
		Optional<CartItem> current = cartItemRepo.findByCart_UserIdAndProductId(userId, item.getProductId());
		if(current.isPresent()) {
			if(product.getQuantity()>item.getQuantity()) {
			CartItem currentItem = current.get();
			currentItem.setQuantity(quantity);
			cartItemRepo.save(currentItem);
			totalCartValue(userId);
			throw new ProductAlreadyInCartException();
			}else {
				throw new ProductQuantityException();
			}
		}else {
			if(product.getQuantity()>item.getQuantity()) {
			item.setCart(cart);
			cartItemRepo.save(item);
			totalCartValue(userId);
			return "Item added in Cart";
		}else {
			new ProductQuantityException();
			return "";
		}
		}
	}
	
	//INCREMENT ITEM QUANTITY BY 1 "+" button

	public String addItemButton(Long userId,Long productId){    //for increasing the quantity of product from cart (+ button) 
		Optional<CartItem> current = cartItemRepo.findByCart_UserIdAndProductId(userId,productId);
		if(current.isPresent()) {
			CartItem currentItem = current.get();
			currentItem.setQuantity(currentItem.getQuantity()+1);
			cartItemRepo.save(currentItem);
			totalCartValue(userId);
			throw new ProductAlreadyInCartException();
		}else {
			throw new ProductNotInCartException();
		}
	}
	
	
	//DECREAMENT ITEM QUANTITY BY 1 BUTTON "-"
	
	public String deleteItemButton(Long userId,Long productId) //for decreasing the quantity by one (- button)
	{
		Optional<CartItem> current = cartItemRepo.findByCart_UserIdAndProductId(userId,productId);
		if(current.isPresent()) {
			CartItem currentItem = current.get();
			currentItem.setQuantity(currentItem.getQuantity()-1);
			cartItemRepo.save(currentItem);
			totalCartValue(userId);
			return "Item quantity modified";
		}else {
			throw new ProductNotInCartException();
		}
	}
	
	//DELETE COMPLETE ITEM FROM CART
	
	public String deleteItem(Long userId,Long productId) {  //delete complete item from the cart
		Optional<CartItem> current = cartItemRepo.findByCart_UserIdAndProductId(userId, productId);
		if(current.isPresent()) {  
			cartItemRepo.delete(current.get());
			totalCartValue(userId);
			return "Item deleted from cart";
		}
		else {
			throw new ProductNotInCartException();
		}
	}
	
	//UPDATE CART VALUE UTILITY METHOD
	
	public void totalCartValue(Long userId) {
		Cart cart = cartRepo.findByUserId(userId).get();
		List<CartItem> items = cartItemRepo.findByCart_UserId(userId);
		Long total = 0L;
		for(CartItem item : items) {
			System.out.println(item.getPrice()+item.getProductId());
			total+=item.getTotalPrice();
		}
		cart.setTotalCost(total);
		cartRepo.save(cart);
	}
	
	//PLACEORDER
	
	@Transactional
	public List<CartDTO> placeOrder(Long userId) { //PlaceOrder to send value to products
		Cart cart = cartRepo.findByUserId(userId).get();
		List<CartItem> items = cartItemRepo.findByCart_UserId(userId);
		List<CartDTO> forProduct = new ArrayList<>();
		for(CartItem item:items) {
			CartDTO product = new CartDTO();
			product.setUserId(userId);
			product.setProductId(item.getProductId());
			product.setQuantity(item.getQuantity());
			product.setTotalPrice(item.getTotalPrice());
			forProduct.add(product);
		}
		cartItemRepo.deleteByCart_UserId(userId);
		cart.setTotalCost(0L);
		return forProduct;
	}
	
	
	//WISHLIST
	
	public void addToWishlist(Long userId, CartDTO item) {
		if(wishListRepo.findByUserIdAndProductId(userId,item.getProductId()).isPresent())
		{
			throw new ProductAlreadyInWishListException();
		}else {
		CartWishlist newWishList = new CartWishlist();
		newWishList.setProductId(item.getProductId());
		newWishList.setQuantity(item.getQuantity());
		newWishList.setPrice(item.getTotalPrice());
		newWishList.setUserId(userId);
		wishListRepo.save(newWishList);
		}
	}
	
	public List<CartWishlist> findWishlistByUserId(Long userId) {
		return wishListRepo.findByUserId(userId);
	}
	
	@Transactional
	public void clearWishList(Long userId) {
		 wishListRepo.deleteByUserId(userId);
	}
	
}
