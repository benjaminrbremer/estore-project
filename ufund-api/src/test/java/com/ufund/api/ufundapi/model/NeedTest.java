package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ufund.api.ufundapi.Need;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
@Tag("Model-tier")
public class NeedTest {

    private ArrayList<String> tags;

    @BeforeEach
    public void setup() {

        tags = new ArrayList<>();
        tags.add("Test");

    }

    @Test
    public void testNeedConstructor() {

        // Setup expected values
        String expectedName1 = "test1";
        String expectedName2 = "test2";
        double expectedPrice1 = 12.69;
        double expectedPrice2 = 420.69;
        int expectedStock1 = 100;
        int expectedStock2 = 1000;
        ArrayList<String> expectedTags1 = new ArrayList<>();
        expectedTags1.add("test1");
        ArrayList<String> expectedTags2 = new ArrayList<>();
        expectedTags2.add("test2");

        // Create needs with expected values
        Need need1 = new Need(
                expectedName1, expectedPrice1, expectedStock1, expectedTags1);
        Need need2 = new Need(
                expectedName2, expectedPrice2, expectedStock2, expectedTags2);

        // Analyze
        assertEquals(expectedName1, need1.getName());
        assertEquals(expectedName2, need2.getName());
        assertEquals(expectedPrice1, need1.getPrice());
        assertEquals(expectedPrice2, need2.getPrice());
        assertEquals(expectedStock1, need1.getStock());
        assertEquals(expectedStock2, need2.getStock());
        assertEquals(expectedTags1, need1.getTags());
        assertEquals(expectedTags2, need2.getTags());

    }

    @Test
    public void testNeedOverloadedConstructor1() {

        // Setup
        String expectedName = "test1";
        double expectedPrice = 12.69;
        int expectedStock = 100;
        ArrayList<String> expectedTags = new ArrayList<>();
        expectedTags.add("test tags");

        // Invoke
        Need need1 = new Need(
                expectedName, expectedPrice, expectedStock, expectedTags);

        // Analyze
        assertEquals(expectedName, need1.getName());
        assertEquals(expectedPrice, need1.getPrice());
        assertEquals(expectedStock, need1.getStock());
        assertEquals(expectedTags, need1.getTags());

    }

    @Test
    public void testNeedOverloadedConstructor2() {

        // Setup
        String expectedName = "test1";
        double expectedPrice = 12.69;
        int expectedStock = 100;
        String expectedImagePath = "test image path";

        // Invoke
        Need need1 = new Need(
                expectedName, expectedPrice, expectedStock, expectedImagePath);

        // Analyze
        assertEquals(expectedName, need1.getName());
        assertEquals(expectedPrice, need1.getPrice());
        assertEquals(expectedStock, need1.getStock());
        assertEquals(expectedImagePath, need1.getImagePath());

    }

    @Test
    public void testNeedOverloadedConstructor3() {

        // Setup
        String expectedName = "test1";
        double expectedPrice = 12.69;
        int expectedStock = 100;

        // Invoke
        Need need1 = new Need(
                expectedName, expectedPrice, expectedStock);

        // Analyze
        assertEquals(expectedName, need1.getName());
        assertEquals(expectedPrice, need1.getPrice());
        assertEquals(expectedStock, need1.getStock());

    }

    @Test
    public void testUpdateName() {

        // Setup Needs and expected values
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        String expectedName1 = "The first one worked!";
        String expectedName2 = "The second one worked!";

        // Change the names of the Needs to the expected names
        need1.updateName(expectedName1);
        need2.updateName(expectedName2);

        // Analyze
        assertEquals(expectedName1, need1.getName());
        assertEquals(expectedName2, need2.getName());

    }

    @Test
    public void testUpdatePrice() {

        // Setup Needs and expected values
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        double expectedPrice1 = 100.00;
        double expectedPrice2 = 200.00;

        // Change the names of the Needs to the expected names
        need1.updatePrice(expectedPrice1);
        need2.updatePrice(expectedPrice2);

        // Analyze
        assertEquals(expectedPrice1, need1.getPrice());
        assertEquals(expectedPrice2, need2.getPrice());

    }

    @Test
    public void testUpdateStock() {

        // Setup Needs and expected values
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        int expectedStock1 = 200;
        int expectedStock2 = 500;

        // Change the names of the Needs to the expected names
        need1.updateStock(200);
        need2.updateStock(500);

        // Analyze
        assertEquals(expectedStock1, need1.getStock());
        assertEquals(expectedStock2, need2.getStock());

    }

    @Test
    public void testHashCode() {

        // Setup Needs and expected values
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        int productCode1 = need1.getProductID();
        int productCode2 = need2.getProductID();

        // Invoke / Analyze
        assertEquals(need1.hashCode(), productCode1);
        assertEquals(need2.hashCode(), productCode2);

    }

    @Test
    public void testEquals() {

        // Setup two equal Needs
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test1", 12.69, 100, this.tags);
        boolean expected = true;

        // Invoke / Analyze
        assertEquals(expected, need1.equals(need2));

    }

    @Test
    public void testEqualsFalse() {

        // Setup two unequal Needs
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        boolean expected = false;

        // Invoke / Analyze
        assertEquals(expected, need1.equals(need2));

    }

    @Test
    public void testEqualsNotNeed() {

        // Setup a need and another object
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Object object = new Object();
        boolean expected = false;

        // Invoke / Analyze
        assertEquals(expected, need1.equals(object));

    }

    @Test
    public void testToString() {

        // Setup Needs and expected values
        Need need1 = new Need(
                "test1", 12.69, 100, this.tags);
        Need need2 = new Need(
                "test2", 420.69, 1000, this.tags);
        int expectedID1 = need1.getProductID();
        int expectedID2 = need2.getProductID();
        String expectedToString1 =
                "{\"ID\":" + expectedID1 +
                        ",\"name\":test1,\"price\":12.69,\"stock\":100}";
        String expectedToString2 =
                "{\"ID\":" + expectedID2 +
                        ",\"name\":test2,\"price\":420.69,\"stock\":1000}";

        // Invoke
        String actualToString1 = need1.toString();
        String actualToString2 = need2.toString();

        // Analyze
        assertEquals(expectedToString1, actualToString1);
        assertEquals(expectedToString2, actualToString2);

    }

}
