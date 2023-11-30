package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.CupboardController;
import com.ufund.api.ufundapi.CupboardDAO;
import com.ufund.api.ufundapi.CupboardFileDAO;
import com.ufund.api.ufundapi.Need;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


@SpringBootTest
@Tag("Controller-tier")
public class CupboardControllerTest {

    private CupboardController cupboardController;
    private CupboardDAO mockCupboardDAO;
    private ArrayList<String> tags;

    @BeforeEach
    public void setupCupboardController() {

        this.mockCupboardDAO = mock(CupboardFileDAO.class);
        this.cupboardController = new CupboardController(this.mockCupboardDAO);
        this.tags = new ArrayList<>();
        tags.add("test");

    }

    @Test
    public void testCreateNeed() throws IOException {

        // Setup some example Needs
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);

        // Simulate successful Need creation
        when(this.mockCupboardDAO.createNeed(need1)).thenReturn(need1);
        when(this.mockCupboardDAO.createNeed(need2)).thenReturn(need2);

        // Invoke
        ResponseEntity<Need> response1 =
                this.cupboardController.createNeed(need1);
        ResponseEntity<Need> response2 =
                this.cupboardController.createNeed(need2);

        // Analyze
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

    }

    @Test
    public void testCreateNeedDuplicate() throws IOException {

        // Setup some example Needs
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        HashMap<Integer, Need> needs = new HashMap<>();
        needs.put(need1.getProductID(), need1);

        // Simulate failed Need creation (need2 is a duplicate)
        when(this.mockCupboardDAO.createNeed(need1)).thenReturn(null);
        when(this.mockCupboardDAO.getAllNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.createNeed(need1);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test
    public void testCreateNeedSaveFailed() {

        // Setup example Need
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);

        // Simulate failed save
        when(this.mockCupboardDAO.createNeed(need1)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.createNeed(need1);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test
    public void testCreateNeedNull() throws IOException {

        // Setup a null Need
        Need need1 = null;

        // Simulate correct CupboardFileDAO functionality
        when(this.mockCupboardDAO.createNeed(need1)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.createNeed(need1);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test 
    public void testDeleteNeed() throws IOException {

        // Setup a Need
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);

        // Simulate successful CupboardFileDAO functionality
        when(this.mockCupboardDAO.deleteNeed(need1.getProductID())).thenReturn(need1);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.deleteNeed(need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testDeleteNeedNull() {

        // Setup / Invoke
        ResponseEntity<Need> response = this.cupboardController.deleteNeed(-1);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testUpdateNeed() throws IOException {

        // Create original and updated Need
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test1 updated", 12.69, 100, this.tags);
        HashMap<Integer, Need> needs = new HashMap<>();
        needs.put(need1.getProductID(), need1);

        // Simulate successful dependent functionality in CupboardFileDAO
        when(this.mockCupboardDAO.updateNeed(need2,
                need1.getProductID())).thenReturn(need2);
        when(this.mockCupboardDAO.getNeed(need1.getProductID())).
                thenReturn(need1);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.updateNeed(need2, need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need2, response.getBody());

    }

    @Test
    public void testUpdateNeedNull() {

        // Setup an original Need and a null Need
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = null;

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.updateNeed(need2, need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testUpdateNeedDoesNotExist() {

        // Setup
        when(this.mockCupboardDAO.getNeed(0)).thenReturn(null);
        Need need1 = new Need(
                "test1", 12.69, 100, new ArrayList<>());

        // Invoke
        ResponseEntity<Need> response = this.cupboardController.updateNeed(
                need1, 0);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testGetNeed() throws IOException {

        // Setup Need and Map
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        HashMap<Integer, Need> needs = new HashMap<>();
        needs.put(need1.getProductID(), need1);
        mockCupboardDAO.createNeed(need1);

        // Simulate proper CupboardFileDAO functionality
        when(this.mockCupboardDAO.getNeed(need1.getProductID())).thenReturn(need1);

        // Invoke
        ResponseEntity<Need> response =
                this.cupboardController.getNeed(need1.getProductID());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testGetNeedNotFound() throws IOException {

        int needID = -1;

        ResponseEntity<Need> response = cupboardController.getNeed(needID);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(response.getBody());

    }

    @Test
    public void testGetAllNeeds() throws IOException {

        // Create Needs and needs map
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        HashMap<Integer, Need> needs = new HashMap<>();
        needs.put(need1.getProductID(), need1);
        needs.put(need2.getProductID(), need2);

        // Simulate successful CupboardFileDAO functionality
        when(this.mockCupboardDAO.getAllNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Map<Integer, Need>> response =
                this.cupboardController.getAllNeeds();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testFindNeeds() throws IOException {

        // Setup a Need
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        HashMap<Integer, Need> needs = new HashMap<>();
        needs.put(need1.getProductID(), need1);

        // Simulate proper CupboardFileDAO functionality
        when(this.mockCupboardDAO.findNeeds(need1.getName())).thenReturn(needs);

        // Invoke
        ResponseEntity<Map<Integer, Need>> response =
                this.cupboardController.findNeeds("test1");

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testFindNeedsEmpty() {

        // Setup / Invoke
        ResponseEntity<Map<Integer, Need>> response =
                this.cupboardController.findNeeds("no needs in Cupboard");

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testGetAllTags() {

        // Setup
        when(this.mockCupboardDAO.getAllTags()).thenReturn(this.tags);

        // Invoke
        ResponseEntity<List<String>> response =
                this.cupboardController.getAllTags();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.tags, response.getBody());

    }

    @Test
    public void testGetAllTagsDoesNotExist() {

        // Setup
        when(this.mockCupboardDAO.getAllTags()).thenReturn(null);

        // Invoke
        ResponseEntity<List<String>> response = this.cupboardController.getAllTags();

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testFilterPrice() {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        HashMap<Integer, Need> cupboard = new HashMap<>();
        cupboard.put(need1.getProductID(), need1);
        cupboard.put(need2.getProductID(), need2);
        when(this.mockCupboardDAO.filterPrice(0, 500)).
                thenReturn(cupboard);

        // Invoke
        /*
        ResponseEntity<Map<Integer, Need>> response =
                this.cupboardController.filterPrice("0-500");
         */
        double[] priceRange = new double[2];
        priceRange[0] = 0;
        priceRange[1] = 500;
        ResponseEntity<Map<Integer, Need>> response =
                this.cupboardController.filterPrice(priceRange);

        // Analyze
        assertEquals(cupboard, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testFilterPriceOutOfRange() {

        // Setup
        when(this.mockCupboardDAO.filterPrice(501, 502)).
                thenReturn(null);
        when(this.mockCupboardDAO.filterPrice(501, 0)).
                thenReturn(null);
        when(this.mockCupboardDAO.filterPrice(-10, 502)).
                thenReturn(null);

        // Invoke
        /*
        ResponseEntity<Map<Integer, Need>> response1 =
                this.cupboardController.filterPrice("501-502");
        ResponseEntity<Map<Integer, Need>> response2 =
                this.cupboardController.filterPrice("501-0");
        ResponseEntity<Map<Integer, Need>> response3 =
                this.cupboardController.filterPrice("-10-502");
         */
        double[] priceRange = new double[2];
        priceRange[0] = 501;
        priceRange[1] = 502;
        ResponseEntity<Map<Integer, Need>> response1 =
                this.cupboardController.filterPrice(priceRange);
        priceRange[0] = 501;
        priceRange[1] = 0;
        ResponseEntity<Map<Integer, Need>> response2 =
                this.cupboardController.filterPrice(priceRange);
        priceRange[0] = -10;
        priceRange[1] = 502;
        ResponseEntity<Map<Integer, Need>> response3 =
                this.cupboardController.filterPrice(priceRange);


        // Analize
        assertNull(response1.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
        assertNull(response2.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertNull(response3.getBody());
        assertEquals(HttpStatus.EXPECTATION_FAILED, response3.getStatusCode());

    }

    @Test
    public void testFilterTags() {

        // Setup
        ArrayList<String> emptyTags = new ArrayList<>();
        ArrayList<String> fullTags = new ArrayList<>();
        fullTags.add("test");
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        HashMap<Integer, Need> cupboard = new HashMap<>();
        cupboard.put(need1.getProductID(), need1);
        cupboard.put(need2.getProductID(), need2);
        when(this.mockCupboardDAO.filterTags(fullTags)).thenReturn(cupboard);

        // Invoke
        ResponseEntity<Map<Integer, Need>> response1 =
                this.cupboardController.filterTags(emptyTags);
        ResponseEntity<Map<Integer, Need>> response2 =
                this.cupboardController.filterTags(fullTags);

        // Analyze
        assertNull(response1.getBody());
        assertEquals(HttpStatus.EXPECTATION_FAILED, response1.getStatusCode());
        assertEquals(cupboard, response2.getBody());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

    }

    @Test
    public void testFilterTagsFailure() {

        // Setup
        ArrayList<String> tags = new ArrayList<>();
        tags.add("failure");
        when(this.mockCupboardDAO.filterTags(tags)).thenReturn(null);

        // Invoke
        ResponseEntity<Map<Integer, Need>> response = this.cupboardController.filterTags(tags);

        // Analyze
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void testFilterNumberSold() {

        // Setup
        Need need1 = new Need(
                "test 1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test 2", 420.69, 1000, this.tags);
        need1.updateStock(50);
        need2.updateStock(50);
        ArrayList<Need> expectedHighLow = new ArrayList<>();
        expectedHighLow.add(need2);
        expectedHighLow.add(need1);
        ArrayList<Need> expectedLowHigh = new ArrayList<>();
        expectedLowHigh.add(need1);
        expectedLowHigh.add(need2);
        when(this.mockCupboardDAO.filterNumberSold(true)).thenReturn(expectedHighLow);
        when(this.mockCupboardDAO.filterNumberSold(false)).thenReturn(expectedLowHigh);

        // Invoke
        ResponseEntity<ArrayList<Need>> responseHighLow =
                this.cupboardController.filterNumberSold(true);
        ResponseEntity<ArrayList<Need>> responseLowHigh =
                this.cupboardController.filterNumberSold(false);

        // Analyze
        assertEquals(expectedHighLow, responseHighLow.getBody());
        assertEquals(HttpStatus.OK, responseHighLow.getStatusCode());
        assertEquals(expectedLowHigh, responseLowHigh.getBody());
        assertEquals(HttpStatus.OK, responseLowHigh.getStatusCode());

    }

    @Test
    public void testFilterNumberSoldNoNeeds() {

        // Setup
        when(this.mockCupboardDAO.filterNumberSold(true)).thenReturn(null);

        // Invoke
        ResponseEntity<ArrayList<Need>> response =
                this.cupboardController.filterNumberSold(true);

        // Analyze
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

}

