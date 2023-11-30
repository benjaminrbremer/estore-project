package com.ufund.api.ufundapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.logging.Logger;

/**
 * The CupboardFileDAO class contains methods called by the <br>
 * CupboardController to help facilitate communication between JSON files <br>
 * and Http requests.
 *
 * Implements the CupboardDAO interface.
 *
 * @author Benjamin Bremer (bwb1113)
 * @author Elijah Sanders (ejs8021)
 * @author Ryan Pedraza (rsp3216)
 * @author Emmanuel Boakye (egb6005)
 */
@Component
public class CupboardFileDAO implements CupboardDAO {

    private final String filename;

    private final ObjectMapper objectMapper;

    /**
     * Map containing all needs in the cupboard mapped to their int ID
     */
    private Map<Integer, Need> needs;

    /**
     * Logger object to help keep track of backend functionality <br>
     * in the terminal
     */
    private static final Logger LOG =
            Logger.getLogger(CupboardFileDAO.class.getName());

    /**
     * The CupboardFileDAO constructor initializes an instance of the
     * cupboard and initializes the HashMap that contains the needs
     */
    public CupboardFileDAO(@Value("${needs.file}")String filename,
                           ObjectMapper objectMapper) {

        this.needs = new HashMap<>();
        this.filename = filename;
        this.objectMapper = objectMapper;
        boolean loadValue = load();
        if (!loadValue) {

            LOG.info("Load failed\n");
            System.err.println("Load failed\n");

        }else {

            LOG.info("Load succeeded: CupboardFileDAO initialized\n");

        }

    }

    /**
     * The load method loads in the state of the Cupboard that <br>
     *      * was stored in a JSON the last time the application was running.
     *      * @return - Boolean true if the load was successful, false if <br>
     *      *           otherwise
     */
    private boolean load() {

        try {

            Need[] needsArray =
                    this.objectMapper.readValue(
                            new File(this.filename), Need[].class);
            for (Need need : needsArray) {

                this.needs.put(need.getProductID(), need);

            }

            return true;

        }catch (IOException ioException) {

            System.err.println(ioException.getMessage());
            return false;

        }

    }

    /**
     * The getNeedsArray method is used by the save method to help <br>
     * the ObjectMapper write the Cupboard to a JSON. It takes the <br>
     * HashMap of the Needs in the Cupboard and converts it to a <br>
     * native Java array of type Need.
     * @return - Need[] the Needs in the Cupboard as a Java native array
     */
    public Need[] getNeedsArray() {

        ArrayList<Need> needsList = new ArrayList<>(this.needs.values());
        Need[] needsArray = new Need[needsList.size()];

        for(int i = 0; i < needsArray.length; i++) {

            needsArray[i] = needsList.get(i);

        }

        return needsArray;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save() {

        try {

            Need[] needsArray = getNeedsArray();
            objectMapper.writeValue(new File(this.filename), needsArray);
            LOG.info("Save succeeded\n");
            return true;

        }catch (IOException ioException) {

            LOG.info("Save failed\n");
            System.err.println(ioException.getMessage());
            return false;

        }

    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Need createNeed(Need need) {

        // Create Need
        if(!this.needs.containsValue(need)) {

            this.needs.put(need.getProductID(), need);
            LOG.info("\t\tCupboardFileDAO created Need\n");
            // Save
            boolean saveResult = save();
            if (saveResult) {

                return needs.get(need.getProductID());

            }else {

                return needs.get(need.getProductID());
                // return null;

            }

        }else{

            LOG.info("\t\tCupboardFileDAO could not create Need: " +
                    "Need already esists.\n");
            return null;

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need deleteNeed(int id) {

        if(this.needs.containsKey(id)) {

            // Delete Need
            Need removed = needs.remove(id);
            LOG.info("\t\tCupboardFileDAO deleted Need\n");

            // Save
            boolean saveResult = save();
            if (saveResult) {

                return removed;

            }else {

                return null;

            }

        }else {

            return null;

        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need getNeed(int id) {

        if(needs.containsKey(id)) {

            return needs.get(id);

        }else {

            return null;

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Need> findNeeds(String name) {

        HashMap<Integer, Need> matchingNeeds = new HashMap<>();

        for(Need need : this.needs.values()) {
            if(need.getName().contains(name)) {

                matchingNeeds.put(need.getProductID(), need);

            }

        }

        return matchingNeeds;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Need> filterPrice(
            double lowPrice, double highPrice) {

        HashMap<Integer, Need> filtered = new HashMap<>();
        Need need;

        if(lowPrice < 0 || lowPrice > highPrice) {

            return null;

        }else {

            for (int id : this.needs.keySet()) {

                need = this.needs.get(id);

                if (need.getPrice() >= lowPrice &&
                        need.getPrice() <= highPrice) {

                    filtered.put(id, need);

                }

            }

            if (!(filtered.isEmpty())) {

                return filtered;

            } else {

                return null;

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Need> filterTags(ArrayList<String> tags) {

        HashMap<Integer, Need> filtered = new HashMap<>();
        Need need;

        if(tags == null || tags.isEmpty()) {

            return null;

        }else {

            for (int id : this.needs.keySet()) {

                need = this.needs.get(id);

                for (String tag : tags) {

                    if (need.getTags().contains(tag)) {

                        filtered.put(id, need);
                        break;

                    }

                }

            }

            if (!(filtered.isEmpty())) {

                return filtered;

            } else {

                return null;

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Need> filterNumberSold(boolean highToLow) {

        // HashMap<Integer, Need> sorted = new HashMap<>();
        ArrayList<Need> sortedList = new ArrayList<>(this.needs.values());
        Collections.sort(sortedList);

        if(this.needs.isEmpty()) {

            return null;

        }else {

            if (highToLow) {

                return sortedList;

            } else {

                ArrayList<Need> reversedList = new ArrayList<>();

                for (Need need : sortedList) {

                    reversedList.add(0, need);

                }

                return reversedList;

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need updateNeed(Need need, int id) {

        // Update Need
        if(this.needs.containsKey(id)) {

            this.needs.get(id).updateStock(need.getStock());
            this.needs.get(id).updateName(need.getName());
            this.needs.get(id).updatePrice(need.getPrice());
            this.needs.get(id).updateImagePath(need.getImagePath());
            this.needs.get(id).updateTags(need.getTags());
            this.needs.get(id).updateDescription(need.getDescription());
            LOG.info("\t\tCupboardFileDAO updated Need");

            // Save
            boolean saveResult = save();
            if (saveResult) {

                return need;

            }else {

                return null;

            }

        }else {

            LOG.info("\t\tCupboardFileDAO could not update Need: " +
                    "ID not found in Cupboard\n");
            return null;

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Need> getAllNeeds() {

        return needs;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllTags() {

        return Need.getAllTags();

    }

}
