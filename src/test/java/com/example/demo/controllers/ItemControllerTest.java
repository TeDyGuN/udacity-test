package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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

public class ItemControllerTest {

    private ItemRepository itemRepository;

    private ItemController itemController;

    @Before
    public void setup() {
        itemRepository = Mockito.mock(ItemRepository.class);
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items_test() {
        List<Item> items = getItemsTest();
        Mockito.when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertEquals(3, responseItems.size());
        assertNotNull(responseItems);
        assertEquals(responseItems, items);
    }

    @Test
    public void get_item_test() {
        Item itemTest = getItemTest();
        when(itemRepository.findById(itemTest.getId())).thenReturn(Optional.of(itemTest));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        assertEquals(itemTest.getId(), item.getId());
        assertEquals(itemTest.getName(), item.getName());
    }

    @Test
    public void get_item_by_name_test() {
        Item item = getItemTest();
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> itemsResponse = response.getBody();
        assertNotNull(itemsResponse);
        assertEquals(1, itemsResponse.size());
        assertEquals(itemsResponse, items);
    }

    public Item getItemTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Dog cookies");
        item.setDescription("Simple dog cookies");
        item.setPrice(new BigDecimal("24.00"));
        return item;
    }

    public List<Item> getItemsTest() {
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
        items.add(getItemTest());
        items.add(item);
        items.add(item2);
        return items;
    }
}
