package com.ufund.api.ufundapi.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.Admin;
import com.ufund.api.ufundapi.Basket;
import com.ufund.api.ufundapi.Helper;
import com.ufund.api.ufundapi.Need;
import com.ufund.api.ufundapi.OutOfStockException;
import com.ufund.api.ufundapi.User;
import com.ufund.api.ufundapi.UserFileDAO;

/**
 * Test the UserFileDAO class
 * 
 * @author Elijah Sanders (ejs8021)
 */
@Tag("Persistence-tier")
public class UserFileDAOTest {
    UserFileDAO userFileDAO;
    ObjectMapper objectMapper;
    
    // test users
    int usersInTestData = 4;
    Admin admin1;
    Admin admin2;
    Helper helper1;
    Helper helper2;

    // data file paths
    String adminFilename = "data\\admins.json";
    String helperFilename = "data\\helpers.json";

    // initial file contents
    String adminFileContents = "[{\"username\":\"admin\",\"passwordHash\":\"x61Ey612Kl2gpFL56FT9weDnpSo4AV8j8+qx2AuTHdRyY036xxzTTrw10Wq3+4qQyB+XURPWx1ONxp3Y3pB37A==\",\"isAdmin\":true},{\"username\":\"admin2\",\"passwordHash\":\"sQnzu7wkTrgkQZF+0G1hi5AI3Qmzvv0bXgc5THBqi7mAsdd4Xll27ASbRt9fEyavWi6m0QP9B8lThf+rDKy8hg==\",\"isAdmin\":true}]";
    String helperFileContents = "[{\"username\":\"donald\",\"passwordHash\":\"CdjXsjReSPPb5C2BiDuc9KXS3iJkkpwKmdCVf8+6PWl7ML9LWzwCGPIRoDdEb72oMZSdRtmhXMMOUDpjR07E5Q==\",\"isAdmin\":false,\"basket\":{\"basketItems\":{},\"basketQuantity\":{}}},{\"username\":\"howard\",\"passwordHash\":\"KrdRI3p4Nlq5euMo7yStWbf1kC/xYIoKLx9YTP9B09O8P0JT+IXKzmfXElWu9oxv4VKsiFmS/9Iw7i/V88pqYA==\",\"isAdmin\":false,\"basket\":{\"basketItems\":{},\"basketQuantity\":{}}}]";

    @BeforeEach
    public void setUpUserFileDAO() throws IOException {
        // test users that should be in JSON files
        admin1 = new Admin("admin", "admin");
        admin2 = new Admin("admin2", "petsname");
        helper1 = new Helper("howard", "password123");
        helper2 = new Helper("donald", "mothersmaidenname");

        // reset the user files
        FileWriter adminWriter = new FileWriter(adminFilename);
        adminWriter.write(adminFileContents);
        adminWriter.close();
        FileWriter helperWriter = new FileWriter(helperFilename);
        helperWriter.write(helperFileContents);
        helperWriter.close();

        objectMapper = new ObjectMapper();
        userFileDAO = new UserFileDAO(helperFilename, adminFilename, objectMapper);
    }

    @Test
    public void testGetUsersAdmins() {
        User result1 = userFileDAO.getUser(admin1.getUsername());
        User result2 = userFileDAO.getUser(admin2.getUsername());
        assertEquals(admin1.getUsername(), result1.getUsername(), "First admin not loaded");
        assertEquals(admin2.getUsername(), result2.getUsername(), "Second admin not loaded");
    }

    @Test
    public void testGetUsersHelpers() {
        User result3 = userFileDAO.getUser(helper1.getUsername());
        User result4 = userFileDAO.getUser(helper2.getUsername());
        assertEquals(helper1.getUsername(), result3.getUsername(), "First helper not loaded");
        assertEquals(helper2.getUsername(), result4.getUsername(), "Second helper not loaded");
    }

    @Test 
    public void testGetAllUsers() {
        User[] result = userFileDAO.getAllUsers();
        assertEquals(usersInTestData, result.length);
    }

    @Test 
    public void testCreateUser() throws IOException {
        Admin newAdmin = new Admin("newadmin", "qwerty");
        User result1 = userFileDAO.createUser(newAdmin.getUsername(), "qwerty", true);
        User result2 = userFileDAO.getUser(newAdmin.getUsername());
        Helper newHelper = new Helper("newhelper", "password");
        User result3 = userFileDAO.createUser(newHelper.getUsername(), "password", false);
        User result4 = userFileDAO.getUser(newHelper.getUsername());
        assertEquals(newAdmin.getUsername(), result1.getUsername(), "createUser didn't return user");
        assertEquals(newAdmin.getUsername(), result2.getUsername(), "User not added");
        assertEquals(newHelper.getUsername(), result3.getUsername(), "createUser didn't return user");
        assertEquals(newHelper.getUsername(), result4.getUsername(), "User not added");
    }

    @Test
    public void testCreateUserDuplicate() throws IOException {
        Admin duplicateAdmin = new Admin(admin1.getUsername(), admin1.getPasswordHash());
        User result1 = userFileDAO.createUser(duplicateAdmin.getUsername(), "admin", true);
        User[] result2 = userFileDAO.getAllUsers();
        assertEquals(null, result1, "createUser should return null when trying to add a duplicate");
        assertEquals(usersInTestData, result2.length, "Wrong number of users after duplicate create");
    }

    @Test
    public void testDeleteUser() throws IOException {
        User expected = userFileDAO.getUser(admin1.getUsername());
        User result1 = userFileDAO.deleteUser(admin1.getUsername());
        User[] result2 = userFileDAO.getAllUsers();
        assertEquals(expected, result1, "DeleteUser didn't return deleted user");
        assertEquals(usersInTestData - 1, result2.length, "Wrong number of users after 1 was deleted");
    }
    
    @Test
    public void testDeleteUserNotFound() throws IOException {
        User result1 = userFileDAO.deleteUser("goose");
        User[] result2 = userFileDAO.getAllUsers();
        assertEquals(null, result1, "DeleteUser didn't return null when user doesn't exist");
        assertEquals(usersInTestData, result2.length, "Wrong number of users after trying to delte user that doesn't exist");
    }

    @Test
    public void testGetBasket() throws IOException, OutOfStockException {
        // add a user with a basket
        Basket expected = new Basket();
        ArrayList<String> tags = new ArrayList<>();
        tags.add("test");
        Need need1 = new Need("test1", 12.69, 100, tags);
        Need need2 = new Need("test2", 420.69, 1000, tags);
        expected.addNeedBasket(need1);
        expected.addNeedBasket(need2);
        helper1.updateBasket(expected);
        userFileDAO.updateBasket(helper1.getUsername(), expected);

        // get the basket
        Basket result = userFileDAO.getBasket(helper1.getUsername());
        assertEquals(expected, result, "Basket not gotten");
    }

    @Test
    public void testGetBasketNotFound() throws IOException {
        // get basket of an admin
        Basket result1 = userFileDAO.getBasket(admin1.getUsername());
        assertEquals(null, result1, "Getting basket of an admin doesn't return null");

        // get basket of a user that doesn't exist
        Basket result2 = userFileDAO.getBasket("goose");
        assertEquals(null, result2, "Getting basket of a user that doesn't exist doesn't return null");
    }

}