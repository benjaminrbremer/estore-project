package com.ufund.api.ufundapi;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The abstract User class stores all user specific data
 * and contains functions shared among all users
 * 
 * @author Elijah Sanders (ejs8021) 
 */
public class User {
    @JsonProperty("username") private String username;
    @JsonProperty("isAdmin") private boolean isAdmin;
    @JsonProperty("passwordHash") private byte[] passwordHash;

    /**
     * Create a new instance of a User with an already hashed password
     * @param username - String username of the user
     */
    public User(@JsonProperty("username") String username, @JsonProperty("isAdmin") boolean isAdmin, @JsonProperty("passwordHash") byte[] passwordHash) {
        this.username = username;
        this.isAdmin = isAdmin;
        this.passwordHash = passwordHash;
    }

    /**
     * Create a new instance of a user with a password string
     * @return
     */
    public User(String username, boolean isAdmin, String pasword) {
        this(username, isAdmin, hashPassword(pasword));
    }
    
    /**
     * Return the username of a user
     * @return - String the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Change the username of a user
     * @param username - String new username of the user
     */
    public void updateUsername(String username) {
        this.username = username;
    }

    /**
     * Returns if the user is an admin
     * @return - boolean True if user is an admin, False otherwise
     */
    @JsonProperty(value="isAdmin") // prevent ObjectMapper from adding "admin" value to JSON
    public boolean isAdmin() {
        return isAdmin;
    };

    /**
     * Hashes a user's password so it can be securely stored
     * @return - String hash of the password
     */
    public static byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }
    
    /**
     * Return the hash of the user's password
     * @return - byte[] password hash
     */
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * The equals method determines if two Users are equal
     * @param other - other User to compare to
     * @return - boolean true if the Users are equal, false if not
     */
    @Override
    public boolean equals(Object other) {

        if(other instanceof User) {

            User otherUser = (User) other;

            if(this.isAdmin == otherUser.isAdmin &&
                    this.username.equals(otherUser.getUsername()) &&
                    this.getPasswordHash() == otherUser.getPasswordHash()) {

                return true;

            }else {

                return false;

            }

        }else {

            return false;

        }

    }
}
