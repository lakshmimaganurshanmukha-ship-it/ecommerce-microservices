package org.tcskart.cart.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartWishlist;

public interface WishlistRepository extends CrudRepository<CartWishlist,Long>{

	List<CartWishlist> findByUserId(Long userId);

	Optional<CartWishlist> findByUserIdAndProductId(Long userId,Long productId);

	void deleteByUserId(Long userId);
	
}
