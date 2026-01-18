package org.tcskart.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartDTO;
import org.tcskart.cart.bean.CartItem;
import org.tcskart.cart.dao.CartItemRepository;
import org.tcskart.cart.dao.CartRepository;
import org.tcskart.cart.dao.WishlistRepository;
import org.tcskart.cart.dto.ProductResponseDTO;
import org.tcskart.cart.exceptions.ProductAlreadyInCartException;
import org.tcskart.cart.exceptions.UserNotFoundException;
import org.tcskart.cart.service.CartService;
import org.tcskart.cart.service.ProductClient;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private ProductClient productClient;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private CartItemRepository cartItemRepo;

    @Mock
    private WishlistRepository wishListRepo;

    @Test
    void testAddToCart_ItemAlreadyInCart() {
        Long userId = 1L;
        CartItem item = new CartItem();
        item.setProductId(101L);
        item.setQuantity(1);

        ProductResponseDTO product = new ProductResponseDTO();
        product.setQuantity(5);
        Mockito.when(productClient.getProductById(101L)).thenReturn(product);

        Cart cart = new Cart();
        Mockito.when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartItem existingItem = new CartItem();
        existingItem.setProductId(101L);
        existingItem.setQuantity(1);
        Mockito.when(cartItemRepo.findByCart_UserIdAndProductId(userId, 101L))
               .thenReturn(Optional.of(existingItem));

        Assertions.assertThrows(ProductAlreadyInCartException.class, () -> {
            cartService.addToCart(userId, item);
        });
    }
    @Test
    void testAddToCart_NewItem() {
        Long userId = 1L;
        Long productId = 101L;

        Cart mockCart = new Cart();
        mockCart.setUserId(userId);
        mockCart.setCartId(1L);

        CartItem newItem = new CartItem();
        newItem.setProductId(productId);
        newItem.setQuantity(2);
        newItem.setPrice(200L); // Ensure totalPrice is non-zero

        ProductResponseDTO productDto = new ProductResponseDTO();
        productDto.setId(productId);
        productDto.setQuantity(10); // Ensure stock is available

        // Mocks
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(mockCart));  // 👈 fix for your issue
        when(productClient.getProductById(productId)).thenReturn(productDto);
        when(cartItemRepo.findByCart_UserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        when(cartItemRepo.findByCart_UserId(userId)).thenReturn(List.of(newItem)); // Needed for totalCartValue()
        when(cartRepo.save(any(Cart.class))).thenReturn(mockCart); // if saving cart

        String result = cartService.addToCart(userId, newItem);
        assertEquals("Item added in Cart", result);
    }
    @Test
    void testPlaceOrder() {
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setUserId(userId);

        CartItem item = new CartItem();
        item.setProductId(101L);
        item.setQuantity(2);
        item.setPrice(300L);

        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepo.findByCart_UserId(userId)).thenReturn(List.of(item));

        List<CartDTO> result = cartService.placeOrder(userId);
        assertEquals(1, result.size());
        verify(cartItemRepo).deleteByCart_UserId(userId);
    }

    @Test
    void testDeleteItem_ItemExists() {
        Long userId = 1L;
        Long productId = 101L;
        CartItem item = new CartItem();
        item.setProductId(productId);

        when(cartItemRepo.findByCart_UserIdAndProductId(userId, productId)).thenReturn(Optional.of(item));
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(new Cart()));
        when(cartItemRepo.findByCart_UserId(userId)).thenReturn(List.of());

        String result = cartService.deleteItem(userId, productId);
        assertEquals("Item deleted from cart", result);
    }
    

    
    public void totalCartValue(Long userId) {
        Optional<Cart> optionalCart = cartRepo.findByUserId(userId);
        if (optionalCart.isEmpty()) {
            throw new UserNotFoundException(); // or log warning
        }

        Cart cart = optionalCart.get();
        List<CartItem> items = cartItemRepo.findByCart_UserId(userId);

        Long total = 0L;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        cart.setTotalCost(total);
        cartRepo.save(cart);
    }


    // Add tests for:
    // - placeOrder()
    // - addItemButton() - expect exception if not present
    // - deleteItemButton() - validate quantity decrement
    // - deleteItem()
    // - getUserCart() - valid and not found
    // - addToWishlist() - with/without duplicate
    // - clearWishList()

}