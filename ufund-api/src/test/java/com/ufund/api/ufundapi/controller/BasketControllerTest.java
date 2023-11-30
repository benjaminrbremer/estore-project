package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@Tag("Controller-tier")
public class BasketControllerTest {

    private UserFileDAO mockUserDAO;
    private CupboardFileDAO mockCupboardDAO;
    private BasketController basketController;
    private Basket basket;
    private final String USERNAME = "Username";
    private Need need1;
    private Need need2;
    private ArrayList<String> tags;

    @BeforeEach
    public void setupBasketController() throws OutOfStockException {

        mockUserDAO = mock(UserFileDAO.class);
        mockCupboardDAO = mock(CupboardFileDAO.class);
        basketController = new BasketController(
                this.mockUserDAO, this.mockCupboardDAO);
        tags = new ArrayList<>();
        tags.add("test");
        need1 = new Need("test 1", 12.69, 100, tags);
        need2 = new Need("test 2", 420.69, 1000, tags);
        basket = new Basket();
        basket.addNeedBasket(need1);
        basket.addNeedBasket(need2);

    }

    @Test
    public void testAddNeedBasket() throws IOException, OutOfStockException {

        // Simulate successful get functionality in UserFileDAO
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);
        when(mockCupboardDAO.getNeed(need2.getProductID())).thenReturn(this.need2);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.addNeedBasket(this.USERNAME,
                        this.need2.getProductID());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.basket, response.getBody());

    }

    @Test
    public void testAddNeedBasketDoesNotExist() throws IOException {

        // Setup
        when(this.mockUserDAO.getBasket(this.USERNAME)).thenReturn(null);
        when(this.mockCupboardDAO.getNeed(this.need1.getProductID())).
                thenReturn(this.need1);

        // Invoke
        ResponseEntity<Basket> response = this.basketController.addNeedBasket(
                this.USERNAME, this.need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testAddNeedBasketNullNeed() throws IOException {

        // Setup
        when(this.mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);
        when(this.mockCupboardDAO.getNeed(this.need1.getProductID())).
                thenReturn(null);

        // Invoke
        ResponseEntity<Basket> response = this.basketController.addNeedBasket(
                this.USERNAME, this.need2.getProductID());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testAddNeedBasketIOException() throws IOException {

        // Simulate an IOException
        doThrow(new IOException()).
                when(this.mockUserDAO).getBasket(this.USERNAME);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.addNeedBasket(this.USERNAME,
                        this.need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testAddNeedBasketOutOfStockException()
            throws OutOfStockException, IOException {

        // Setup
        this.need1.updateStock(1);
        this.need2.updateStock(1);

        // Simulate correct get functionality
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);
        when(mockCupboardDAO.getNeed(need1.getProductID())).thenReturn(this.need1);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.addNeedBasket(this.USERNAME,
                        this.need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertNull(response.getBody());

    }


    @Test
    public void testRemoveNeedBasket() throws IOException {

        // Simulate successful get functionality
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.
                        removeNeedBasket(this.USERNAME, this.need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.basket, response.getBody());

    }

    @Test
    public void testRemoveNeedBasketIOException() throws IOException {

        // Simulate an IOException
        doThrow(new IOException()).
                when(mockUserDAO).getBasket(this.USERNAME);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.removeNeedBasket(
                        this.USERNAME, this.need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testRemoveNeedBasketNullPointerException() throws IOException {

        // Setup
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);
        when(mockCupboardDAO.getNeed(need1.getProductID())).
                thenReturn(this.need1);
        doThrow(new IOException()).when(this.mockUserDAO).
                updateBasket(this.USERNAME, this.basket.
                        removeNeedBasket(this.need1.getProductID()));

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.removeNeedBasket(
                        this.USERNAME, this.need1.getProductID());

        // Analyze
        assertNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void testFundBasket() throws IOException {

        // Simulate successful get functionality
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.fundBasket(this.USERNAME);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.basket, response.getBody());

    }

    @Test
    public void testFundBasketIOException() throws IOException {

        // Simulate an IOException
        doThrow(new IOException()).
                when(mockUserDAO).getBasket(this.USERNAME);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.fundBasket(this.USERNAME);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testFundBasketOutOfStockException()
            throws IOException, OutOfStockException {

        // Setup
        this.need1.updateStock(0);
        this.need2.updateStock(0);

        // Simulate correct get functionality
        when(mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);

        // Invoke
        ResponseEntity<Basket> response =
                this.basketController.fundBasket(this.USERNAME);

        // Analyze
        assertEquals(HttpStatus.EXPECTATION_FAILED,
                response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testGetBasket() throws IOException {

        // Setup
        when(this.mockUserDAO.getBasket(this.USERNAME)).thenReturn(this.basket);

        // Invoke
        ResponseEntity<Basket> result =
                this.basketController.getBasket(this.USERNAME);

        // Analyze
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(result.getBody(), this.basket);

    }

    @Test
    public void testGetBasketDoesNotExist() throws IOException {

        // Setup
        when(this.mockUserDAO.getBasket("I do not exist")).
                thenReturn(null);

        // Invoke
        ResponseEntity<Basket> result =
                this.basketController.getBasket("I do not exist");

        // Analyze
        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(result.getBody());

    }

    @Test
    public void testGetBasketIOException() throws IOException {

        // Setup
        when(this.mockUserDAO.getBasket("I do not exist")).
                thenThrow(new IOException());

        // Invoke
        ResponseEntity<Basket> result =
                this.basketController.getBasket("I do not exist");

        // Analyze
        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertNull(result.getBody());

    }

}
