package com.ufund.api.ufundapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Admin represents a User with admin privalages
 * 
 * @author Elijah Sanders (ejs8021)
 */
public class Admin extends User {

    /**
     * Create a new instance of an Admin user with admin privaleges
     * @param username - String username of the user
     * @param passwordHash - byte[] hash of the user's password
     */
    public Admin(@JsonProperty("username") String username, @JsonProperty("passwordHash") byte[] passwordHash) {
        super(username, true, passwordHash);
    }

    public Admin(String username, String password) {
        super(username, true, password);
    }

}
