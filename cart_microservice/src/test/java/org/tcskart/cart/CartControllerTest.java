package org.tcskart.cart;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.tcskart.cart.bean.Cart;
import org.tcskart.cart.bean.CartWishlist;
import org.tcskart.cart.config.JwtUtilValidateToken;
import org.tcskart.cart.controller.Controller;
import org.tcskart.cart.service.CartService;

@WebMvcTest(Controller.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtilValidateToken jwtUtil;

    private String jwtToken;
    private Long userId;

    @BeforeEach
    public void setup() {
        jwtToken = "Bearer fake.jwt.token";
        userId = 1L;

        // Mock JWT token parsing
        when(jwtUtil.getClaimId("fake.jwt.token")).thenReturn(userId);
    }

    private RequestPostProcessor jwt() {
        return request -> {
            request.addHeader("Authorization", jwtToken);
            return request;
        };
    }

    @WithMockUser(roles = "USER")
    @Test
    public void testGetUserCart() throws Exception {
        Cart cart = new Cart();
        cart.setUserId(userId);
        Optional<Cart> cartOpt = Optional.of(cart);

        when(cartService.getUserCart(userId)).thenReturn(cartOpt);

        mockMvc.perform(get("/carts/{userid}/viewcart", userId)
                .with(jwt()))
                .andExpect(status().isOk());
    }
    
    @WithMockUser(roles = "USER")
    @Test
    public void testGetWishlist() throws Exception {
        when(cartService.findWishlistByUserId(userId)).thenReturn(List.of(new CartWishlist()));

        mockMvc.perform(get("/carts/{userid}/wishlist", userId)
                .with(jwt()))
            .andExpect(status().isOk());
    }
}

