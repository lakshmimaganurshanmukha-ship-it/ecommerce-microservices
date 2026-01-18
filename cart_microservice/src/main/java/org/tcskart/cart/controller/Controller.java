package org.tcskart.cart.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartDTO;
import org.tcskart.cart.bean.CartItem;
import org.tcskart.cart.bean.CartWishlist;
import org.tcskart.cart.config.JwtUtilValidateToken;
import org.tcskart.cart.service.CartService;


import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/carts")
public class Controller {
	
	@Autowired
	CartService service;
	
	@Autowired
	JwtUtilValidateToken tokenValidator;
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{userid}/viewcart") //To view cart
	public Optional<Cart> getUserCart(HttpServletRequest httpServletRequest){
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.getUserCart(tokenValidator.getClaimId(token));
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{userid}/wishlist")
	public List<CartWishlist> getWishlist(HttpServletRequest httpServletRequest) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.findWishlistByUserId(tokenValidator.getClaimId(token));
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/{userid}/placeorder")
	public List<CartDTO> placeOrder(HttpServletRequest httpServletRequest){
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.placeOrder(tokenValidator.getClaimId(token));		  
	}
	
	@GetMapping("/{userid}/viewcart/viewitems") //To show only the added cart items 
	public List<CartItem> getCartItems(@PathVariable long userid){
		return service.getCartItems(userid);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/{userid}/additems") //Add item to cart
	public String addToCart(HttpServletRequest httpServletRequest,@RequestBody CartItem item){
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.addToCart(tokenValidator.getClaimId(token),item); 
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/{userid}/wishlist/additems")
	public String addToWishlist(HttpServletRequest httpServletRequest,@RequestBody CartDTO item ) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		service.addToWishlist(tokenValidator.getClaimId(token), item);
		return "Item Added to the wishlist";
	}
	
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{userid}/wishlist/clearitems")
	public String deleteWishlist(HttpServletRequest httpServletRequest) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		service.clearWishList(tokenValidator.getClaimId(token));
		return "Wishlist cleared";
	}
	
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{userid}/deleteitems/{productid}")
	public String deleteFromCart(HttpServletRequest httpServletRequest,@PathVariable Long productid) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.deleteItem(tokenValidator.getClaimId(token),productid);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{userid}/reduceitems/{productid}")
	public String reduceItemsFromCart(HttpServletRequest httpServletRequest,@PathVariable Long productid) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.deleteItemButton(tokenValidator.getClaimId(token), productid);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{userid}/increaseitems/{productid}") // + reduce button implementation
	public String increaseItemsFromCart(HttpServletRequest httpServletRequest,@PathVariable Long productid) {
		String token=httpServletRequest.getHeader("Authorization").substring(7);
		return service.addItemButton(tokenValidator.getClaimId(token), productid);
	}
	
	
}
