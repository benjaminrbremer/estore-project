package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.UserController;
import com.ufund.api.ufundapi.UserDAO;
import com.ufund.api.ufundapi.UserFileDAO;
import com.ufund.api.ufundapi.User;
import com.ufund.api.ufundapi.Admin;
import com.ufund.api.ufundapi.Helper;

@SpringBootTest
@Tag("Controller-tier")
public class UserControllerTest {
    private UserController userController;
    private UserDAO mockUserDao;

    @BeforeEach
    public void setupUserController() {
        mockUserDao = mock(UserFileDAO.class);
        userController = new UserController(mockUserDao);
    }

    @Test
    @SuppressWarnings("null")
    public void testCreateUser() throws IOException {
        User helper = new Helper("helper1", "password123");        
        when(mockUserDao.createUser(helper.getUsername(), "password123", false)).thenReturn(helper);

        Map<String, String> login = new HashMap<>();
        login.put("username", "helper1");
        login.put("password", "password123");
        ResponseEntity<User> result = userController.createUser(login);
    
        assertEquals(HttpStatus.CREATED, result.getStatusCode(), "Status code not OK");
        assertEquals(helper.getUsername(), result.getBody().getUsername(), "Helper not created successfully");
    }

    @Test
    @SuppressWarnings("null")
    public void testCreateAdmin() throws IOException {
        User admin = new Admin("admin", "admin");
        when(mockUserDao.createUser(admin.getUsername(), "admin", true)).thenReturn(admin);
        
        Map<String, String> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "admin");

        
        ResponseEntity<User> result = userController.createAdmin(login);
        assertEquals(HttpStatus.CREATED, result.getStatusCode(), "Status code not OK");
        assertEquals(admin.getUsername(), result.getBody().getUsername(), "Admin not created successfully");

    }

    @Test
    public void testCreateUserDuplicate() throws IOException {
        User helper = new Helper("helper1", "password123");
        Map<String, String> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "admin");

        // simulate a successful addition, then conflict
        when(mockUserDao.createUser(helper.getUsername(), "password123", false)).thenReturn(helper).thenReturn(null);
        
        // try to create the same user twice
        userController.createUser(login);
        ResponseEntity<User> result = userController.createUser(login);
        
        ResponseEntity<User> expected = new ResponseEntity<>(HttpStatus.CONFLICT);

        assertEquals(expected, result, "HTTP Status conflict not sent");
    }

    @Test
    public void testCreateUserIOException() throws IOException {

        // Setup
        String username = "username";
        String password = "password";
        String isAdmin = "false";
        Map<String, String> login = new HashMap<>();
        login.put("username", username);
        login.put("password", password);
        login.put("isAdmin", isAdmin);
        doThrow(new IOException()).when(this.mockUserDao).createUser(username, password, false);

        // Invoke
        ResponseEntity<User> response = this.userController.createUser(login);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testDeleteUser() throws IOException {
        User helper = new Helper("helper1", "password123");
        when(mockUserDao.deleteUser(helper.getUsername())).thenReturn(helper);
        
        ResponseEntity<User> result = userController.deleteUser(helper.getUsername());
        ResponseEntity<User> expected = new ResponseEntity<User>(helper, HttpStatus.OK);

        assertEquals(expected, result, "User not deleted successfully");
    }

    @Test
    public void testDeleteUserNotFound() throws IOException {
        User helper = new Helper("helper1", "password123");
        when(mockUserDao.deleteUser(helper.getUsername())).thenReturn(null);
        
        ResponseEntity<User> result = userController.deleteUser(helper.getUsername());
        ResponseEntity<User> expected = new ResponseEntity<User>(HttpStatus.NOT_FOUND);

        assertEquals(expected, result, "NOT_FOUND error not returned");
    }

    @Test
    public void testDeleteUserIOException() throws IOException {

        // Setup
        String username = "username";
        String password = "password";
        User helper = new Helper(username, password);
        doThrow(new IOException()).when(this.mockUserDao).
                deleteUser(helper.getUsername());

        // Invoke
        ResponseEntity<User> response = this.userController.
                deleteUser(helper.getUsername());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    public void testGetUser() throws IOException {
        User helper = new Helper("helper1", "password123");
        when(mockUserDao.getUser(helper.getUsername())).thenReturn(helper);
        
        ResponseEntity<User> result = userController.getUser(helper.getUsername());
        ResponseEntity<User> expected = new ResponseEntity<User>(helper, HttpStatus.OK);

        assertEquals(expected, result, "User not found");
    }

    @Test
    public void testGetUserNotFound() throws IOException {
        User helper = new Helper("helper1", "password123");
        when(mockUserDao.getUser(helper.getUsername())).thenReturn(null);
        
        ResponseEntity<User> result = userController.getUser(helper.getUsername());
        ResponseEntity<User> expected = new ResponseEntity<User>(HttpStatus.NOT_FOUND);

        assertEquals(expected, result, "NOT_FOUND error not returned");
    }

    /*
    @Test
    public void testGetUserIOException() throws IOException {

        // Setup
        User helper = new Helper("helper1");
        // doThrow(new IOException()).when(this.mockUserDao).getUser(helper.getUsername());
        // when(this.mockUserDao.getUser(helper.getUsername())).thenThrow(new Exception());
        doThrow(new IOException()).when(this.mockUserDao.getUser(helper.getUsername()));

        // Invoke
        ResponseEntity<User> response = this.userController.
                getUser(helper.getUsername());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

    }
     */

    @Test
    public void testIsAdmin() throws IOException {
        User admin = new Admin("admin", "admin");
        User helper = new Helper("helper1", "password123");

        when(mockUserDao.getUser(admin.getUsername())).thenReturn(admin);
        when(mockUserDao.getUser(helper.getUsername())).thenReturn(helper);
        
        ResponseEntity<Boolean> result1 = userController.isAdmin(admin.getUsername());
        ResponseEntity<Boolean> result2 = userController.isAdmin(helper.getUsername());

        ResponseEntity<Boolean> expected1 = new ResponseEntity<Boolean>(true, HttpStatus.OK);
        ResponseEntity<Boolean> expected2 = new ResponseEntity<Boolean>(false, HttpStatus.OK);

        assertEquals(expected1, result1, "Admin doesn't show as admin");
        assertEquals(expected2, result2, "Helper shows as admin");
    }

    @Test
    public void testIsAdminNotFound() throws IOException {
        User admin = new Admin("admin", "admin");

        when(mockUserDao.getUser(admin.getUsername())).thenReturn(null);
        
        ResponseEntity<Boolean> result = userController.isAdmin(admin.getUsername());
        ResponseEntity<Boolean> expected = new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);

        assertEquals(expected, result, "NOT_FOUND error not returned");
    }

    /*
    @Test
    public void testIsAdminIOException() throws IOException {

        // Setup
        User helper = new Helper("helper1");
        doThrow(new IOException()).when(this.mockUserDao).getUser(helper.getUsername());

        // Invoke
        ResponseEntity<Boolean> response = this.userController.
                isAdmin(helper.getUsername());

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

    }
     */

    @Test
    public void testLogin() throws IOException {
        User admin = new Admin("admin", "admin");
        Map<String, String> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "admin");
        
        when(mockUserDao.getUser(admin.getUsername())).thenReturn(admin);
        
        ResponseEntity<String> result = userController.login(login);
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Statust code not OK");
        assertNotNull(result.getBody(), "No session key returned");
    }

    @Test
    public void testLoginNotFound() throws IOException {
        when(mockUserDao.getUser("fakeUser")).thenReturn(null);
        Map<String, String> login = new HashMap<>();
        login.put("username", "fakeUser");
        login.put("password", "fakePassword");

        ResponseEntity<String> result = userController.login(login);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode(), "Should return Not Found");
    }

    @Test
    public void testLoginIncorrect() throws IOException {
        User admin = new Admin("admin", "admin");
        Map<String, String> login = new HashMap<>();
        login.put("username", "admin");
        login.put("password", "wrong");
        
        when(mockUserDao.getUser(admin.getUsername())).thenReturn(admin);
        
        ResponseEntity<String> result = userController.login(login);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode(), "Statust code not Unauthorized");
    }

}
