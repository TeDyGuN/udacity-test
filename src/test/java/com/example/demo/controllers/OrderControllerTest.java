package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
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

public class OrderControllerTest {

    private OrderRepository orderRepository;

    private OrderController orderController;

    private User testUser;

    private UserOrder testUserOrder;

    @Before
    public void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        testUser = getTestUser();
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(testUser);

        testUserOrder = new UserOrder();
        testUserOrder.setId(1L);
        testUserOrder.setUser(testUser);
        testUser.setCart(testUser.getCart());
    }

    @Test
    public void submit_test() {
        when(orderRepository.findById(testUserOrder.getId())).thenReturn(Optional.of(testUserOrder));
        when(orderRepository.save(testUserOrder)).thenReturn(testUserOrder);

        ResponseEntity<UserOrder> response = orderController.submit(testUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
    }

    @Test
    public void get_orders_for_user_test() {
        List<UserOrder> testUserOrders = new ArrayList<>();
        testUserOrders.add(testUserOrder);
        when(orderRepository.findByUser(testUser)).thenReturn(testUserOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(testUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
        assertEquals(userOrders, testUserOrders);
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
