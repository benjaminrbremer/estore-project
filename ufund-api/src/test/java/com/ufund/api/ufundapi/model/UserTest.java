package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ufund.api.ufundapi.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@SpringBootTest
@Tag("Model-tier")
public class UserTest {
    
    @Test
    public void testAdminConstructor() {
        Admin admin = new Admin("admin", "admin");
        assertEquals("admin", admin.getUsername(), "Admin created with incorrect username");
        assertEquals(true, admin.isAdmin(), "Admin doesn't show as admin");
    }

    @Test
    public void testHelperConstructor() {
        Helper helper = new Helper("helper1", "password123");
        assertEquals("helper1", helper.getUsername(), "Helper created with incorrect username");
        assertEquals(false, helper.isAdmin(), "Helper shows as admin");
    }

    @Test 
    public void testUpdateUsername() {
        Helper helper = new Helper("helper1", "password123");
        String newUsername = "helper2";
        helper.updateUsername(newUsername);
        assertEquals(newUsername, helper.getUsername(), "Username was not updated");
    }

    @Test
    public void testUpdateBasket() throws OutOfStockException {
        Helper helper = new Helper("helper1", "password123");
        assertEquals(0, helper.getBasket().getBasketItems().size(), "Basket not empty when User was initialized");
        Basket newBasket = new Basket();
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Test");
        Need need1 = new Need("test1", 12.69, 100, tags);
        Need need2 = new Need("test2", 420.69, 1000, tags);
        newBasket.addNeedBasket(need1);
        newBasket.addNeedBasket(need2);
        helper.updateBasket(newBasket);
        assertEquals(newBasket, helper.getBasket());
    }

    @Test
    public void testHashPassword() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] expected = md.digest("password123".getBytes(StandardCharsets.UTF_8));
        byte[] actual = User.hashPassword("password123");
        assertArrayEquals(expected, actual, "Incorrect password hash");
    }

    @Test
    public void testGetPasswordHash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] expected = md.digest("password123".getBytes(StandardCharsets.UTF_8));
        
        Helper helper = new Helper("helper1", "password123");
        byte[] actual = helper.getPasswordHash();
        assertArrayEquals(expected, actual, "Incorrect password hash");
    }

}
