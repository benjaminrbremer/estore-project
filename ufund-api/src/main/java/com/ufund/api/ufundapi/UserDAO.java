package com.ufund.api.ufundapi;

import java.io.IOException;

/**
 * The UserDAO interface provides the method definitions for
 * facilitating communication the data storage and the UserController
 * 
 * @author Elijah Sanders (ejs8021)
 */
public interface UserDAO {
    
    /**
     * Add a user to the list of users
     * @param username - String Username of the user to be added
     * @param password - String password of the user to be added
     * @param isAdmin - boolean true if the user is an admin, false if they're a helper
     * @return - User the user added, null if user already exists
     */
    public User createUser(String username, String password, boolean isAdmin) throws IOException;

    /**
     * Delete a user
     * @param user - User The user being deleted
     * @return - User the user removed, null if user does not exist 
     */
    public User deleteUser(String username) throws IOException;

    /**
     * Get a user based on their username
     * @param username - String username of the user to get
     * @return - User the user with the given username, null if they don't exist
     */
    public User getUser(String username) throws IOException;

    /**
     * Gets an array of all users
     * @return User[] array of all users
     */
    public User[] getAllUsers() throws IOException;

    /**
     * Get the basket of a user with a given username
     * @param username - String username of the user whose basket to get
     * @return - Basket the user's basket, null if the user doesn't exist or they aren't a helper
     */
    public Basket getBasket(String username) throws IOException;

    /**
     * Update the basket of a user with a given username
     * @param username - String username of the user whose basket to update
     * @param newBasket - The new basket to be given to the user, null if the user doesn't exist or they aren't a helper
     */
    public Basket updateBasket(String username, Basket newBasket) throws IOException;


}
