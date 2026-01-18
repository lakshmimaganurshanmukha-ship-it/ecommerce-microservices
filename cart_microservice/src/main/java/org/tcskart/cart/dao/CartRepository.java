package org.tcskart.cart.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends CrudRepository<Cart,Long> {

	Optional<Cart> findByUserId(Long userId);
}
