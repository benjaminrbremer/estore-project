package com.ufund.api.ufundapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CupboardDAO interface provides the method definitions for the <br>
 * methods necessary to facilitate communication between the <br>
 * CupboardFileDAO and the CupboardController.
 *
 * @author Benjamin Bremer (bwb1113)
 * @author Elijah Sanders (ejs8021)
 * @author Ryan Pedraza (rsp3216)
 * @author Emmanuel Boakye (egb6005)
 */
public interface CupboardDAO {

    /**
     * Allows a single need to be added to the needs HashMap
     * @param need - Need the need to be added to the needs HashMap
     */
    public Need createNeed(Need need);

    /**
     * The save method saves the current state of the Cupboard to a <br>
     * JSON file.
     * @return - Boolean true if the save is successful, false if otherwise
     */
    public boolean save();

    /**
     * The deleteNeed method removes a Need from the Cupboard
     * @param id - int the id of the Need to be removed from the Cupboard
     * @return - Need the Need removed from the Cupboard, null if failed
     */
    public Need deleteNeed(int id);

    /**
     * Get a single need by id
     * @param id - int the id of the Need to get from the Cupboard
     * @return Need object with matching id, null if none exists
     */
    public Need getNeed(int id);

    /**
     * Find all needs whose name matches the name parameter
     * @param name - String to search Needs for
     * @return HashMap<Integer, Need> with matching needs ID's and names.
     */
    public Map<Integer, Need> findNeeds(String name);

    /**
     * The filterPrice method takes in a price range and returns the Needs <br>
     * in the Cupboard that fit within that price range.
     * @param lowPrice - int the low end of the price range.
     * @param highPrice - int the high end of the price range.
     * @return - Map<Integer, Need> The Needs in the Cupboard within that <br>
     *           price range.
     */
    public HashMap<Integer, Need> filterPrice(
            double lowPrice, double highPrice);

    /**
     * The filterTags method takes in an ArrayList of String tags <br>
     * and returns all of the Needs in the Cupboard that have <br>
     * at least one of those tags.
     * @param tags - ArrayList<String> the String tags to search for
     * @return - HashMap<Integer, Need> the Needs in the Cupboard that <br>
     *           have one or more of the tags specified in tags.
     */
    public HashMap<Integer, Need> filterTags(ArrayList<String> tags);

    /**
     * The filterNumberSold method sorts the Needs in the Cupboard <br>
     * by the number of that Need that has been sold, either <br>
     * high to low or low to high.
     * @param highToLow - true if filtering high to low, false if <br>
     *                    filtering low to high.
     * @return - ArrayList<Need> the sorted list of Needs.
     */
    public ArrayList<Need> filterNumberSold(boolean highToLow);

    /**
     * Update an old Need with a new Need
     * @param need - Need the new Need
     * @param id - int the id of the old Need to be updated
     * @return need - Need the updated Need, null if failed
     */
    public Need updateNeed(Need need, int id);

    /**
     * Get the ArrayList of all Needs
     * @return - Map<Integer, Needs> the HashMap of all Needs in the Cupboard
     */
    public Map<Integer, Need> getAllNeeds();

    /**
     * The getAllTags method returns the ArrayList of Strings of all <br>
     * the tags that are stored in the static variable in the Need <br>
     * class. It returns null if there are not currently any Needs in <br>
     * the Cupboard.
     * @return - ArrayList<String> all of the tags that are stored in <br>
     *           the static variable in the Need class, or null if <br>
     *           there are not any Needs currently in the Cupboard.
     */
    public List<String> getAllTags();

}
