package org.tcskart.cart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice     //access exception globally
public class CartGlobalExceptionHandler {
	
	@ExceptionHandler(ProductAlreadyInCartException.class)
	public ResponseEntity<String> handleProductAlreadyPresentException(ProductAlreadyInCartException exception){
		return new ResponseEntity<>("Hey have a look the item was already present in your Cart!!",HttpStatus.OK);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception){
		return new ResponseEntity<>("User not found !!", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ProductNotInCartException.class)
	public ResponseEntity<String> handleProductNotInCartException(ProductNotInCartException exception){
		return new ResponseEntity<>("Product not present in cart", HttpStatus.OK);
	}
	
	@ExceptionHandler(ProductAlreadyInWishListException.class)
	public ResponseEntity<String> handleProductAlreadyInWishListException(ProductAlreadyInWishListException exception){
		return new ResponseEntity<>("Product is already in wishlist", HttpStatus.OK);
	}
	@ExceptionHandler(ProductQuantityException.class)
	public ResponseEntity<String> handleProductQuantityException(ProductQuantityException exception){
		return new ResponseEntity<>("Sorry the desired quantity is not available ", HttpStatus.NOT_ACCEPTABLE);
	}
}

