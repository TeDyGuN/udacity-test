package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserController userController;

    @Before
    public void setup() {
        userController = new UserController();
        userRepository = Mockito.mock(UserRepository.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        CartRepository cartRepository = Mockito.mock(CartRepository.class);
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_test() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("admin12345");
        request.setConfirmPassword("admin12345");
        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void create_user_test_with_password_too_short() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("admin");
        request.setPassword("admin");
        request.setConfirmPassword("admin");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void get_user_test() {
        User testUser = getTestUser();
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.findById(testUser.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    private static User getTestUser() {
        User testUser = new User();
        testUser.setUsername("admin");
        testUser.setId(1L);
        testUser.setPassword("admin");
        return testUser;
    }

    @Test
    public void get_user_by_username_test() {
        User user = getTestUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User userResponse = response.getBody();
        assertEquals(userResponse.getId(), user.getId());
        assertEquals(userResponse.getUsername(), user.getUsername());
    }
}
