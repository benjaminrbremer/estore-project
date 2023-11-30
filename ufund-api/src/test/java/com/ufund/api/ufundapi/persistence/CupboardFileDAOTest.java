package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.CupboardDAO;
import com.ufund.api.ufundapi.CupboardFileDAO;
import com.ufund.api.ufundapi.Need;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@Tag("Persistence-tier")
public class CupboardFileDAOTest {

    private ObjectMapper mockObjectMapper;
    private Need[] testNeeds;
    private CupboardDAO cupboardFileDAO;
    private ArrayList<String> tags;

    @BeforeEach
    public void setupCupboardFileDAO() throws IOException {

        mockObjectMapper = mock(ObjectMapper.class);

        this.tags = new ArrayList<>();
        Need.clearAllTags();
        this.tags.add("test");

        testNeeds = new Need[3];
        testNeeds[0] = new Need(
                "test1",12.69, 100, this.tags);
        testNeeds[1] = new Need(
                "test2",420.69, 1000, this.tags);
        testNeeds[2] = new Need(
                "need3",500.00, 10000, this.tags);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the Need array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"),
                        Need[].class))
                .thenReturn(testNeeds);
        cupboardFileDAO = new CupboardFileDAO(
                "doesnt_matter.txt",mockObjectMapper);

    }

    @Test
    public void testCreateNeed() {

        // Setup
        Need need1 = new Need(
                "create1", 12.69, 100, this.tags);

        // Invoke
        Need result = this.cupboardFileDAO.createNeed(need1);

        // Analyze
        assertEquals(need1, result);

    }

    @Test
    public void testCreateNeedDuplicate() {

        // Setup
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);

        // Invoke
        Need result = this.cupboardFileDAO.createNeed(need1);

        // Analyze
        assertNull(result);

    }

    @Test
    public void testFindNeeds() {

        // Setup Expected
        Map<Integer, Need> expectedFoundNeeds = new HashMap<>();
        expectedFoundNeeds.put(this.testNeeds[0].getProductID(), this.testNeeds[0]);
        expectedFoundNeeds.put(this.testNeeds[1].getProductID(), this.testNeeds[1]);

        // Invoke
        Map<Integer, Need> foundNeeds =
                this.cupboardFileDAO.findNeeds("test");

        // Analyze
        assertEquals(expectedFoundNeeds.size(), foundNeeds.size());
        for(int i : foundNeeds.keySet()) {

            assertEquals(expectedFoundNeeds.get(i), foundNeeds.get(i));

        }

    }

    @Test
    public void testDeleteNeed() {

        // Setup expected Needs map after deletion
        Map<Integer, Need> expectedNeeds = new HashMap<>();
        expectedNeeds.put(this.testNeeds[1].getProductID(), this.testNeeds[1]);
        expectedNeeds.put(this.testNeeds[2].getProductID(), this.testNeeds[2]);

        // Invoke
        Need result =
                this.cupboardFileDAO.deleteNeed(this.testNeeds[0].getProductID());

        // Analyze
        assertEquals(this.testNeeds[0], result);
        Map<Integer, Need> finalNeeds = this.cupboardFileDAO.getAllNeeds();
        assertEquals(expectedNeeds.size(), finalNeeds.size());
        for(int i : finalNeeds.keySet()) {

            assertEquals(expectedNeeds.get(i), finalNeeds.get(i));

        }

    }

    @Test
    public void testDeleteNeedDoesNotExist() {

        // Setup / Invoke
        Need result = this.cupboardFileDAO.deleteNeed(0);

        // Analyze
        assertNull(result);

    }

    @Test
    public void testUpdateNeed() {

        // Setup
        Need updated = new Need(
                "updated1", 1000, 10000, this.tags);

        // Invoke
        Need result = this.cupboardFileDAO.updateNeed(
                updated, this.testNeeds[0].getProductID());

        // Analyze
        assertEquals(updated, result);

    }

    @Test
    public void testUpdateNeedDoesNotExist() {

        // Setup
        Need updated = new Need(
                "updated1", 1000, 10000, this.tags);

        // Invoke
        Need result = this.cupboardFileDAO.updateNeed(updated, 0);

        // Analyze
        assertNull(result);

    }

    @Test
    public void testGetNeed() {

        // Setup / Invoke
        Need result = this.cupboardFileDAO.getNeed(
                this.testNeeds[0].getProductID());

        // Analyze
        assertEquals(this.testNeeds[0], result);

    }

    @Test
    public void testGetNeedDoesNotExist() {

        // Setup / Invoke
        Need result = this.cupboardFileDAO.getNeed(0);

        // Analyze
        assertNull(result);

    }

    @Test
    public void testGetAllTags() {

        // Setup / Invoke
        List<String> result = this.cupboardFileDAO.getAllTags();

        // Analyze
        assertEquals(this.tags, result);

    }

    /*
    @Test
    public void testSaveIOException() throws IOException {

        // Setup
        Need newNeedsDelete[] = new Need[2];
        newNeedsDelete[0] = this.testNeeds[0];
        newNeedsDelete[1] = this.testNeeds[1];
        doThrow(new IOException()).when(this.mockObjectMapper).
                writeValue(new File(
                        "doesnt_matter.txt"), newNeedsDelete);

        Need updated = new Need(
                "updated1", 1000, 10000, this.tags);
        Need[] newNeedsUpdate = new Need[2];
        newNeedsUpdate[0] = updated;
        newNeedsUpdate[1] = this.testNeeds[1];
        doThrow(new IOException()).when(this.mockObjectMapper).
                writeValue(new File(
                        "doesnt_matter.txt"), newNeedsUpdate);

        Need created = new Need(
                "created1", 1000, 10000, this.tags);
        Need newNeedsCreate[] = new Need[3];
        newNeedsCreate[0] = updated;
        newNeedsCreate[1] = this.testNeeds[1];
        newNeedsCreate[2] = created;
        doThrow(new IOException()).when(this.mockObjectMapper).
                writeValue(new File(
                        "doesnt_matter.txt"), newNeedsCreate);

        // Invoke (call a method in CupboardFileDAO that will force a save
        Need deleteResult = this.cupboardFileDAO.deleteNeed(
                this.testNeeds[2].getProductID());
        Need updateResult = this.cupboardFileDAO.updateNeed(
                updated, this.testNeeds[0].getProductID());
        Need createResult = this.cupboardFileDAO.createNeed(created);

        // Analyze
        assertNull(deleteResult);
        assertNull(updateResult);
        assertEquals(createResult, created);
        // assertNull(createResult);

    }
     */

    @Test
    public void testFilterPrice() {

        // Setup
        HashMap<Integer, Need> expected1 = new HashMap<>();
        expected1.put(this.testNeeds[0].getProductID(), this.testNeeds[0]);
        HashMap<Integer, Need> expected2 = new HashMap<>();
        expected2.put(this.testNeeds[1].getProductID(), this.testNeeds[1]);
        expected2.put(this.testNeeds[2].getProductID(), this.testNeeds[2]);

        // Invoke
        HashMap<Integer, Need> response1 =
                this.cupboardFileDAO.filterPrice(0, 20);
        HashMap<Integer, Need> response2 =
                this.cupboardFileDAO.filterPrice(400, 500);

        // Analyze
        assertEquals(expected1, response1);
        assertEquals(expected2, response2);

    }

    @Test
    public void testFilterPriceOutOfRange() {

        // Setup / Invoke
        HashMap<Integer, Need> response1 =
                this.cupboardFileDAO.filterPrice(-10, 0);
        HashMap<Integer, Need> response2 =
                this.cupboardFileDAO.filterPrice(100, 0);
        HashMap<Integer, Need> response3 =
                this.cupboardFileDAO.filterPrice(501, 502);

        // Analyze
        assertNull(response1);
        assertNull(response2);
        assertNull(response3);

    }

    @Test
    public void testFilterTagsAllCases() {

        // Test order -
        //     1 - all needs ("test" tag)
        //     2 - first need ("Test 1" tag)
        //     3 - second need ("Test 2" tag)
        //     4 - third need ("Test 3" tag)
        //     5 - null (empty tags input list)
        //     6 - null (tag that matches to no needs)
        //     7 - null (null tags input list)

        // Setup
        ArrayList<String> newTags1 = new ArrayList<>();
        newTags1.add("Test 1");
        newTags1.add("test");
        this.testNeeds[0].updateTags(newTags1);
        ArrayList<String> newTags2 = new ArrayList<>();
        newTags2.add("Test 2");
        newTags2.add("test");
        this.testNeeds[1].updateTags(newTags2);
        ArrayList<String> newTags3 = new ArrayList<>();
        newTags3.add("Test 3");
        newTags3.add("test");
        this.testNeeds[2].updateTags(newTags3);
        HashMap<Integer, Need> expected1 = new HashMap<>();
        expected1.put(this.testNeeds[0].getProductID(), this.testNeeds[0]);
        expected1.put(this.testNeeds[1].getProductID(), this.testNeeds[1]);
        expected1.put(this.testNeeds[2].getProductID(), this.testNeeds[2]);
        HashMap<Integer, Need> expected2 = new HashMap<>();
        expected2.put(this.testNeeds[0].getProductID(), this.testNeeds[0]);
        HashMap<Integer, Need> expected3 = new HashMap<>();
        expected3.put(this.testNeeds[1].getProductID(), this.testNeeds[1]);
        HashMap<Integer, Need> expected4 = new HashMap<>();
        expected4.put(this.testNeeds[2].getProductID(), this.testNeeds[2]);
        HashMap<Integer, Need> expected5 = null;
        HashMap<Integer, Need> expected6 = null;
        HashMap<Integer, Need> expected7 = null;

        // Invoke
        ArrayList<String> input1 = new ArrayList<>();
        input1.add("test");
        ArrayList<String> input2 = new ArrayList<>();
        input2.add("Test 1");
        ArrayList<String> input3 = new ArrayList<>();
        input3.add("Test 2");
        ArrayList<String> input4 = new ArrayList<>();
        input4.add("Test 3");
        ArrayList<String> input5 = new ArrayList<>();
        ArrayList<String> input6 = new ArrayList<>();
        input6.add("I do not exist");
        ArrayList<String> input7 = null;
        HashMap<Integer, Need> response1 =
                this.cupboardFileDAO.filterTags(input1);
        HashMap<Integer, Need> response2 =
                this.cupboardFileDAO.filterTags(input2);
        HashMap<Integer, Need> response3 =
                this.cupboardFileDAO.filterTags(input3);
        HashMap<Integer, Need> response4 =
                this.cupboardFileDAO.filterTags(input4);
        HashMap<Integer, Need> response5 =
                this.cupboardFileDAO.filterTags(input5);
        HashMap<Integer, Need> response6 =
                this.cupboardFileDAO.filterTags(input6);
        HashMap<Integer, Need> response7 =
                this.cupboardFileDAO.filterTags(input7);

        // Analyze
        assertEquals(expected1, response1);
        assertEquals(expected2, response2);
        assertEquals(expected3, response3);
        assertEquals(expected4, response4);
        assertEquals(expected5, response5);
        assertEquals(expected6, response6);
        assertEquals(expected7, response7);

    }

    @Test
    public void testFilterNumberSold() {

        // Setup
        this.testNeeds[0].updateStock(50);
        this.testNeeds[1].updateStock(50);
        this.testNeeds[2].updateStock(50);
        ArrayList<Need> expectedHighLow = new ArrayList<>();
        expectedHighLow.add(this.testNeeds[2]);
        expectedHighLow.add(this.testNeeds[1]);
        expectedHighLow.add(this.testNeeds[0]);
        ArrayList<Need> expectedLowHigh = new ArrayList<>();
        expectedLowHigh.add(this.testNeeds[0]);
        expectedLowHigh.add(this.testNeeds[1]);
        expectedLowHigh.add(this.testNeeds[2]);

        // Invoke
        ArrayList<Need> responseHighLow =
                this.cupboardFileDAO.filterNumberSold(true);
        ArrayList<Need> responseLowHigh =
                this.cupboardFileDAO.filterNumberSold(false);

        // Analyze
        assertEquals(expectedHighLow, responseHighLow);
        assertEquals(expectedLowHigh, responseLowHigh);

    }

    @Test
    public void testFilterNumberSoldNoNeeds() {

        // Setup
        this.cupboardFileDAO.deleteNeed(this.testNeeds[0].getProductID());
        this.cupboardFileDAO.deleteNeed(this.testNeeds[1].getProductID());
        this.cupboardFileDAO.deleteNeed(this.testNeeds[2].getProductID());

        // Invoke
        ArrayList<Need> response =
                this.cupboardFileDAO.filterNumberSold(true);

        // Analyze
        assertNull(response);

    }

}
