package com.ufund.api.ufundapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Helper represents a user without admin privaleges
 * 
 * @author Elijah Sanders (ejs8021)
 */
public class Helper extends User {

    @JsonProperty("basket") private Basket basket;

    /**
     * Create a new instance of a Helper user without admin privaleges, with an already hashed password
     * @param username - String username of the user
     * @param passwordHash - byte[] hash of User's password
     */
    public Helper(@JsonProperty("username") String username, @JsonProperty("passwordHash") byte[] passwordHash) {
        super(username, false, passwordHash);
        basket = new Basket();
    }

    /**
     * Create a new instance of a Helper user without admin privaleges, with a password string
     * @param username - String username of the user
     * @param password - String the user's password
     */
    public Helper(String username, String password) {
        super(username, false, password);
        basket = new Basket();
    }

    /**
     * Returns the user's basket
     * @return - Basket the user's basket
     */
    public Basket getBasket() {
        return basket;
    }

    /**
     * Replace the user's basket with a new basket
     * @param newBasket - Basket the new basket
     */
    public void updateBasket(Basket newBasket) {
        basket = newBasket;
    }

}
