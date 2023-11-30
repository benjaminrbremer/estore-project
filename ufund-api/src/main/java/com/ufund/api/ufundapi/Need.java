package com.ufund.api.ufundapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * The Need class stores all necessary data for a need and contains the
 * functions for creating and updating it.
 *
 * @author Benjamin Bremer (bwb1113)
 */
public class Need implements Comparable<Need> {

    /**
     * A static int to store the next ID that will get assigned to a new Need
     */
    private static int NEED_ID = 1000;

    /**
     * A static String denoting the filepath of the default image
     */
    private static final String DEFAULT_IMAGE_PATH =
            "data/images/need-default.jpg";

    /**
     * An ArrayList that holds all of the categorization tags for all Needs.
     */
    private static List<String> ALL_TAGS = new ArrayList<>();

    /**
     * A static String to hold the default Need description.
     */
    private static final String DEFAULT_DESCRIPTION = "Support this charity!";

    /**
     * The name of the need
     */
    @JsonProperty("name") private String name;

    /**
     * The price of the need
     */
    @JsonProperty("price") private double price;

    /**
     * The number of items in the cupboard of this particular need
     * (The number of items in stock
     *  Ex. There are *stock* number of *name* in stock)
     */
    @JsonProperty("stock") private int stock;

    /**
     * Unique identifier of a need
     */
    @JsonProperty("productID") private final int productID;

    /**
     * The String representing the filepath of the image used to <br>
     * represent the need. Defaulted to DEFAULT_IMAGE_PATH in constructor.
     */
    @JsonProperty("image") private String imagePath;

    /**
     * An ArrayList of Strings representing the categories that the <br>
     * Need falls into. Used for enhanced search functionality.
     */
    @JsonProperty("tags") private List<String> tags;

    /**
     * An int to track how many of this Need have been sold.
     */
    @JsonProperty("numberSold") private int numberSold;

    /**
     * A String to store the description of this Need
     */
    @JsonProperty("description") private String description;

    /**
     * Creates a new instance of a need
     * @param name - String the name of the need
     * @param price - double the price of the need
     * @param stock - int the number of items in the cupboard of this
     *                particular need
     * @param tags - ArrayList<String> the list of tags to add to the Need.
     * @param imagePath - String the file path of the image for the Need
     * @param description - String the description of the Need
     */
    public Need(@JsonProperty("name") String name,
                @JsonProperty("price") double price,
                @JsonProperty("stock") int stock,
                @JsonProperty("tags") ArrayList<String> tags,
                @JsonProperty("image") String imagePath,
                @JsonProperty("description") String description) {

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productID = NEED_ID;
        this.imagePath = imagePath;
        this.tags = tags;
        addTagsToAll(this.tags);
        NEED_ID ++;
        this.numberSold = 0;
        this.description = description;

    }

    /**
     * Creates a new instance of a need
     * @param name - String the name of the need
     * @param price - double the price of the need
     * @param stock - int the number of items in the cupboard of this
     *                particular need
     * @param tags - ArrayList<String> the list of tags to add to the Need.
     */
    public Need(String name, double price, int stock, ArrayList<String> tags) {

        this(name, price, stock, tags, DEFAULT_IMAGE_PATH, DEFAULT_DESCRIPTION);

    }

    /**
            * Creates a new instance of a need
     * @param name - String the name of the need
     * @param price - double the price of the need
     * @param stock - int the number of items in the cupboard of this
            *                particular need
     * @param imagePath - String the file path of the image for the Need
     */
    public Need(String name, double price, int stock, String imagePath) {

        this(name, price, stock, new ArrayList<>(), imagePath,
                DEFAULT_DESCRIPTION);

    }

    /**
     * Creates a new instance of a need
     * @param name - String the name of the need
     * @param price - double the price of the need
     * @param stock - int the number of items in the cupboard of this
     *                particular need
     */
    public Need(String name, double price, int stock) {

        this(name, price, stock, new ArrayList<>(), DEFAULT_IMAGE_PATH,
                DEFAULT_DESCRIPTION);

    }

    /**
     * The addTagsToAll method takes in a List<String> of tags and adds <br>
     * all of the tags that are not already in the static ALL_TAGS <br>
     * list to the list.
     * @param tags - List<String> the list of tags to be added to the <br>
     *               ALL_TAGS list.
     */
    private void addTagsToAll(List<String> tags) {

        for(String tag : tags) {

            if(!ALL_TAGS.contains(tag)) {

                ALL_TAGS.add(tag);

            }

        }

    }

    /**
     * Returns the name of the need
     * @return - String the name of the need
     */
    public String getName() {

        return this.name;

    }

    /**
     * Returns the price of the need
     * @return - double the price of the need
     */
    public double getPrice() {

        return this.price;

    }

    /**
     * Returns how many of this need are in the cupboard
     * (Returns the number of this item in stock)
     * @return - int the number of this item in stock
     */
    public int getStock() {

        return this.stock;

    }

     /**
     * Returns the unique integer id of the need
     * @return - int the unique id of the need
     */
    @JsonProperty(value="productID") // prevent ObjectMapper from adding "admin" value to JSON
    public int getProductID() {
        
        return this.productID;

    }

    /**
     * The getImagePath method returns the filepath of the Need's image.
     * @return - String the filepath of the Need's image.
     */
    public String getImagePath() {

        return this.imagePath;

    }

    /**
     * The getTags method returns the ArrayList of String tags <br>
     * associated with the Need.
     * @return - ArrayList<String> The String tags associated with this Need
     */
    public List<String> getTags(){

        return this.tags;

    }

    /**
     * The getNumberSold method returns the int that represents the <br>
     * total number of this Need that have been sold.
     * @return - int the total number of this Need that have been sold.
     */
    public int getNumberSold() {

        return this.numberSold;

    }

    /**
     * The getDescription method returns the String description for the Need.
     * @return - String the description for the Need
     */
    public String getDescription() {

        return this.description;

    }

    /**
     * The getAllTags method returns the ArrayList of all String tags <br>
     * that are shared by all Needs.
     * @return - ArrayList<String> The String tags that are shared by all Needs
     */
    public static List<String> getAllTags() {

        return ALL_TAGS;

    }

    /**
     * Updates the name of the need
     * @param name - String the new name for the need
     */
    public void updateName(String name) {

        this.name = name;

    }

    /**
     * Updates the price of the need
     * @param price - double the new price for the need
     */
    public void updatePrice(double price) {

        this.price = price;

    }

    /**
     * Changes how many of this need are in stock. Also adds to numberSold <br>
     * the number of Needs sold.
     * @param stock - int the new stock
     */
    public void updateStock(int stock) {

        int stockDifference = this.stock - stock;
        if(stockDifference > 0) {

            this.numberSold += stockDifference;

        }
        this.stock -= stockDifference;

    }

    /**
     * The updateImagePath method takes in a new String denoting the new <br>
     * image path for the Need and sets it to the local image path variable.
     * @param imagePath - String the new filepath of the image for the Need
     */
    public void updateImagePath(String imagePath) {

        this.imagePath = imagePath;

    }

    /**
     * The updateTags method takes in a List of Strings, sets the local <br>
     * list of tags to that List and adds that List to the static <br>
     * list of tags.
     * @param tags - List<String> the List of tags to add
     */
    public void updateTags(List<String> tags) {

        addTagsToAll(tags);
        this.tags = tags;

    }

    /**
     * The updateDescription method updates the description for the Need.
     * @param description - String the new description for the Need.
     */
    public void updateDescription (String description) {

        this.description = description;

    }

    /**
     * The clearAllTags method clears the static ALL_TAGS variable. <br>
     * Mostly useful for unit testing.
     */
    public static void clearAllTags() {

        ALL_TAGS.clear();

    }

    /**
     * The removeTags method takes in a List of Strings and removes those <br>
     * tags from the local List but not from the static List.
     * @param tags - List<String> the List of tags to remove
     */
    /*
    public void removeTags(List<String> tags) {

        this.tags.removeAll(tags);

    }
     */

    /**
     * The hashCode method returns the productID for the Need, which will <br>
     * be an int unique to that need.
     * @return - int Need's productID to be used as the hash code for the Need
     */
    @Override
    public int hashCode() {

        return this.productID;

    }

    /**
     * The equals method determines if two Need objects are equal based <br>
     * on if their name, price, and stock are equal.
     * @param other - other Object (should be type Need) to be compared
     * @return - boolean true if the Objects are the same,
     *                   false if otherwise
     */
    @Override
    public boolean equals(Object other) {

        if(other instanceof Need) {

            Need otherNeed = (Need) other;

            if(this.name.equals(otherNeed.getName()) &&
                    this.stock == otherNeed.getStock() &&
                    this.price == otherNeed.getPrice()) {

                return true;

            }else {

                return false;

            }

        } else {

            return false;

        }

    }

    /**
     * The compareTo method is used to sort the Needs in the Cupboard <br>
     * by the number sold.
     * @param need the object to be compared.
     * @return - int
     */
    @Override
    public int compareTo(Need need) {

        return need.getNumberSold() - this.numberSold;

    }

    /**
     * Need's toString returns the data members of the Need in JSON format
     * @return - String the Need's data in JSON format
     */
    @Override
    public String toString() {

        return "{\"ID\":" + this.productID +
                ",\"name\":" + this.name +
                ",\"price\":" + this.price +
                ",\"stock\":" + this.stock + "}";

    }

}
