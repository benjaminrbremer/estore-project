package com.ufund.api.ufundapi;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements the UserDAO interface to store, access, and manupulate Users stored in JSON file
 * 
 * @author Elijah Sanders (ejs8021)
 */
@Component
public class UserFileDAO implements UserDAO {

    // Map containing all the Users mapped to their username
    private Map<String, User> users;

    // Paths of JSON files for helper and admin objects
    private String helperFilename;
    private String adminFilename;

    private ObjectMapper objectMapper;

    /**
     * UserFileDAO constructor initializes the HashMap containing the users
     * using a given ObjectMapper, getting the users from a given file
     * @param filename - String the file to load users from
     * @param objectMapper - ObjectMapper provides JSON to/from Java object deserialization/serialization
     * 
     * @throws IOException if there was a failure loading the file
     */
    public UserFileDAO(@Value("${helpers.file}") String helperFilename, @Value("${admins.file}") String adminFilename, ObjectMapper objectMapper) throws IOException {
        this.helperFilename = helperFilename;
        this.adminFilename = adminFilename;
        this.objectMapper = objectMapper;

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(Need.class, new NeedKeyDeserializer());
        this.objectMapper.registerModule(simpleModule);

        load();
    }

    /**
     * Loads {@linkplain User users} from the JSON files into hashMap
     * @throws IOException when file can't be accessed or read
     */
    public boolean load() throws IOException {
        users = new HashMap<>();

        // deserialize the helper JSON objects 
        Helper[] helperArray = objectMapper.readValue(new File(helperFilename), Helper[].class);

        // add users to hashMap
        for(Helper helper : helperArray) {
            users.put(helper.getUsername(), helper);
        }

        // deserialize the admin JSON objects
        Admin[] adminArray = objectMapper.readValue(new File(adminFilename), Admin[].class);

        // add admins to hashmap
        for(Admin admin : adminArray) {
            users.put(admin.getUsername(), admin);
        }

        return true;
    }

    /**
     * Save Users to the JSON files
     * @throws IOException
     */
    private boolean save() throws IOException {
        // make an ArrayList with all the helpers and with all the admins
        ArrayList<Helper> helperArrayList = new ArrayList<Helper>();
        ArrayList<Admin> adminArrayList = new ArrayList<Admin>();
        for(User user : users.values()) {
            if(user instanceof Helper)
                helperArrayList.add((Helper) user);
            else if(user instanceof Admin)
                adminArrayList.add((Admin) user);
        }
        Helper[] helperArray = helperArrayList.toArray(new Helper[helperArrayList.size()]);
        Admin[] adminArray = adminArrayList.toArray(new Admin[adminArrayList.size()]);

        // serialize the User objects into JSON
        objectMapper.writeValue(new File(helperFilename), helperArray);
        objectMapper.writeValue(new File(adminFilename), adminArray);

        return true;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User createUser(String username, String password, boolean isAdmin) throws IOException {
        if(users.containsKey(username)) {
            return null;
        }
        if(isAdmin) {
            Admin admin = new Admin(username, password);
            users.put(admin.getUsername(), admin);
            save();
            return admin;
        } else {
            Helper helper = new Helper(username, password);
            users.put(helper.getUsername(), helper);
            save();
            return helper;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User deleteUser(String username) throws IOException{
        User user = users.remove(username);
        save();
        return user;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User[] getAllUsers() {
        User[] userArray = new User[users.size()];
        int i = 0;
        for(User user : users.values()) {
            userArray[i] = user;
            i++;
        }
        return userArray;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Basket getBasket(String username) throws IOException  {
        if(users.containsKey(username)) {
            if(users.get(username) instanceof Helper) {
                Helper helper = (Helper) users.get(username);
                return helper.getBasket();
            }
        }
        return null;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Basket updateBasket(String username, Basket newBasket) throws IOException {
        if(users.containsKey(username)) {
            if(users.get(username) instanceof Helper) {
                Helper helper = (Helper) users.get(username);
                helper.updateBasket(newBasket);
                save();
                return newBasket;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
    
}
