package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class CartTestController {

    private CartRepository cartRepository;

    private CartController cartController;

    private User testUser;

    private List<Item> testItems;

    Cart testCart;

    @Before
    public void setup() {
        cartController = new CartController();
        cartRepository = Mockito.mock(CartRepository.class);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);

        testUser = getTestUser();
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(testUser);
        testItems = getItemsTest();
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(testItems.get(0)));
        testCart = testUser.getCart();
    }

    @Test
    public void add_to_car_test() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(testItems.get(0).getId());
        modifyCartRequest.setUsername(testUser.getUsername());
        modifyCartRequest.setQuantity(1);
        when(cartRepository.save(testUser.getCart())).thenReturn(testCart);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(4, testCart.getItems().size());
        assertEquals(testCart.getItems(), cart.getItems());
    }

    @Test
    public void remove_from_cart_test() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(testItems.get(0).getId());
        modifyCartRequest.setUsername(testUser.getUsername());
        modifyCartRequest.setQuantity(1);

        testItems.remove(testItems.get(0));
        testCart.setItems(testItems);
        when(cartRepository.save(testCart)).thenReturn(testCart);

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(2, testCart.getItems().size());
        assertEquals(testCart.getItems(), cart.getItems());
    }

    private static User getTestUser() {
        User testUser = new User();
        testUser.setUsername("admin");
        testUser.setId(1L);
        testUser.setPassword("admin");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(testUser);
        cart.setItems(getItemsTest());
        testUser.setCart(cart);
        return testUser;
    }

    public static List<Item> getItemsTest() {
        Item item3 = new Item();
        item3.setId(1L);
        item3.setName("Dog cookies");
        item3.setDescription("Simple dog cookies");
        item3.setPrice(new BigDecimal("24.00"));
        Item item = new Item();
        item.setId(2L);
        item.setName("cat cookies");
        item.setDescription("Simple cat cookies");
        item.setPrice(new BigDecimal("100.00"));
        Item item2 = new Item();
        item2.setId(3L);
        item2.setName("bird cookies");
        item2.setDescription("Simple bird cookies");
        item2.setPrice(new BigDecimal("50.00"));
        List<Item> items = new ArrayList<>();
        items.add(item3);
        items.add(item);
        items.add(item2);
        return items;
    }
}
