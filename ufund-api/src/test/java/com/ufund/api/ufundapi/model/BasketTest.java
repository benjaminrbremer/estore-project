package com.ufund.api.ufundapi.model;

import com.ufund.api.ufundapi.Basket;
import com.ufund.api.ufundapi.Need;

import com.ufund.api.ufundapi.OutOfStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("Model-tier")
public class BasketTest {

    private ArrayList<String> tags;

    @BeforeEach
    public void setup() {

        this.tags = new ArrayList<>();
        this.tags.add("test");

    }

    @Test
    public void testBasketConstructor() {

        // Setup / Invoke
        Basket basket1 = new Basket();
        Basket basket2 = new Basket();

        // Analyze
        assertNotNull(basket1.getBasketItems());
        assertNotNull(basket2.getBasketItems());
        assertNotNull(basket1.getBasketQuantity());
        assertNotNull(basket2.getBasketQuantity());

    }

    @Test
    public void testAddNeedBasket() throws OutOfStockException{

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        Basket basket = new Basket();
        int expectedQuantity1 = 3;
        int expectedQuantity2 = 2;
        HashMap<Need, Integer> expectedQuantityMap = new HashMap<>();
        expectedQuantityMap.put(need1, expectedQuantity1);
        expectedQuantityMap.put(need2, expectedQuantity2);
        HashMap<Integer, Need> expectedItemsMap = new HashMap<>();
        expectedItemsMap.put(need1.getProductID(), need1);
        expectedItemsMap.put(need2.getProductID(), need2);

        // Invoke
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);

        // Analyze
        assertEquals(expectedQuantity1, basket.getBasketQuantity().get(need1));
        assertEquals(expectedQuantity2, basket.getBasketQuantity().get(need2));
        assertEquals(expectedQuantityMap, basket.getBasketQuantity());
        assertEquals(expectedItemsMap, basket.getBasketItems());

    }

    @Test
    public void testAddNeedBasketException() throws OutOfStockException {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 1, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 0, this.tags);
        Basket basket = new Basket();

        // Invoke / Analyze
        basket.addNeedBasket(need1);
        assertThrows(OutOfStockException.class,
                () -> basket.addNeedBasket(need1));
        assertThrows(OutOfStockException.class,
                () -> basket.addNeedBasket(need2));

    }

    @Test
    public void testRemoveNeedBasket() throws OutOfStockException {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        Basket basket = new Basket();
        int expectedQuantity1 = 3;
        HashMap<Need, Integer> expectedQuantityMap = new HashMap<>();
        expectedQuantityMap.put(need1, expectedQuantity1);
        HashMap<Integer, Need> expectedItemsMap = new HashMap<>();
        expectedItemsMap.put(need1.getProductID(), need1);

        // Invoke
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);
        basket.removeNeedBasket(need1.getProductID());
        basket.removeNeedBasket(need1.getProductID());
        basket.removeNeedBasket(need2.getProductID());
        basket.removeNeedBasket(need2.getProductID());

        // Analyze
        assertEquals(expectedQuantity1, basket.getBasketQuantity().get(need1));
        assertEquals(expectedQuantityMap, basket.getBasketQuantity());
        assertEquals(expectedItemsMap, basket.getBasketItems());

    }

    @Test
    public void testFundBasket() throws OutOfStockException {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        Basket basket = new Basket();
        int expectedItemsMapSize = 0;
        int expectedQuantityMapSize = 0;

        // Invoke
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);
        basket.removeNeedBasket(need1.getProductID());
        basket.removeNeedBasket(need1.getProductID());
        basket.removeNeedBasket(need2.getProductID());
        basket.removeNeedBasket(need2.getProductID());
        basket.fundBasket();

        // Analyze
        assertEquals(expectedItemsMapSize, basket.getBasketItems().size());
        assertEquals(expectedQuantityMapSize, basket.getBasketQuantity().size());

    }

    @Test
    public void testFundBasketException() throws OutOfStockException {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        Basket basket = new Basket();
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need2);
        basket.addNeedBasket(need2);
        need1.updateStock(0);
        need2.updateStock(1);

        // Invoke / Analyze
        assertThrows(OutOfStockException.class, () -> basket.fundBasket());

    }

}
