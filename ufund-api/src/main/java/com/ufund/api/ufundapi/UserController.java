package com.ufund.api.ufundapi;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The UserController handles HTTP GET, POST, UPDATE, and
 * DELETE requests related to users and login
 * 
 * @author Elijah Sanders (ejs8021)
 */
@RestController
@RequestMapping("user")
public class UserController {
    
    /**
     * The UserDAO obkect that allows the controller to communicate
     * with the JSON files
     */
    private final UserDAO userDAO;

    // Logger object to help keep track of backend functionality in the terminal
    private static final Logger LOG =
            Logger.getLogger(CupboardFileDAO.class.getName());

    /**
     * The UserController copnstructor creates a new UserFileDAO object to
     * interface with the JSON files
     */
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Takes a username and password from the body of an HTTP request and creates a new user
     * in UserFileDAO, as long as a user with that username doesn't already exist
     * @param loginInfo - Map containing login info in the form {"username":"u", "password":"p"}
     * @return ResponseEntity with new user and HTTP status CREATED if the User is succesfully
     * created, CONFLICT if it already exists, or INTERNAL_SERVER_ERROR if an IOException is thrown
     */
    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> loginInfo) {
        LOG.info("POST /user");
        try {
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            User user = new User(username, false, password);
            if(userDAO.createUser(username, password, false) == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            else
                return new ResponseEntity<User>(user, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Takes a username and password from the body of an HTTP request and creates a new admin
     * in UserFileDAO, as long as a user with that username doesn't already exist
     * @param loginInfo - Map containing login info in the form {"username":"u", "password":"p"}
     * @return ResponseEntity with new user and HTTP status CREATED if the User is succesfully
     * created, CONFLICT if it already exists, or INTERNAL_SERVER_ERROR if an IOException is thrown
     */
    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@RequestBody Map<String, String> loginInfo) {
        LOG.info("POST /user/admin");
        try {
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            User user = new User(username, true, password);
            if(userDAO.createUser(username, password, true) == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            else
                return new ResponseEntity<User>(user, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Takes a username from the URL of an HTTP request and removes the associated
     * user from the UserFileDAO
     * @param username - String username of the user to be deleted
     * @return ResponseEntity with the deleted user and HTTP status OK if deleted,
     * NOT_FOUND if user doesn't exist, and INTERNAL_SERVER_ERROR if an IOException is thrown
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<User> deleteUser(@PathVariable String username) {
        LOG.info("DELETE /user/"  + username);
        try {
            User user = userDAO.deleteUser(username);
            if(user == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a user with a given username
     * @param username - String username of the user to get
     * @return ResponseEntity with the User and HTTP status of OK if found, NOT_FOUND
     * if user doesn't exist, or INTERNAL_SERVER_ERROR if there is an IOException
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        LOG.info("GET /user/" + username);
        try {
            User user = userDAO.getUser(username);
            if(user == null)
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds with true if a user is an admin, false if they are a helper
     * @param username - String username of the user to check
     * @return ResponseEntity with Boolean (true if the user is an admin, false
     * if they aren't) and HTTP status OK if the user exists, NOT_FOUND if they
     * don't exist, and INTERNAL_SERVER_ERROR if there is an IOEXception
     */
    @GetMapping("/admin/{username}")
    public ResponseEntity<Boolean> isAdmin(@PathVariable String username) {
        LOG.info("GET /user/admin/" + username);
        try {
            User user = userDAO.getUser(username);
            if(user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(user.isAdmin(), HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Attempts to login a user. The username and password should be passed in the request body.
     * @param loginInfo - Map containing login info in the form {"username":"u", "password":"p"}
     * @return ResponseEntity<String> with a session key and HTTP status OK is the login was successful,
     * NOT_FOUND if a user with the given username doesn't exist, UNAUTHORIZED if the password is incorrect,
     * or INTERNAL_SERVER_ERROR if there is an IOException 
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginInfo) {
        try {
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            User user = userDAO.getUser(username);
            LOG.info("GET /user/login/" + username);
            if(user == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else if(Arrays.equals(User.hashPassword(password), user.getPasswordHash())) {
                return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout a username with a given username passed in the path.
     * @param username - String username of the user to log out
     * @return - ResponseEntity<Boolean> True if the logout was successful, False if it wasn't (including
     * if the user wasn't logged in to begin with). Http Status NOT_FOUND if the username doesn't exist, 
     * INTERNAL_SERVER_ERROR if there's an IOException, or OK otherwise
     */
    @PostMapping("/logout/{username}")
    public ResponseEntity<Boolean> logout(@PathVariable String username) {
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }
}
