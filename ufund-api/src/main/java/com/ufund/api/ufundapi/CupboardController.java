package com.ufund.api.ufundapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The CupboardController class handles Http GET, POST, UPDATE, and <br>
 * DELETE requests and communicates with the CupboardFileDAO.
 *
 * @author Benjamin Bremer (bwb113)
 * @author Elijah Sanders (ejs8021)
 * @author Ryan Pedraza (rsp3216)
 * @author Emmanuel Boakye (egb6005)
 */
@RestController
@RequestMapping("ufundapi")
public class CupboardController {

    /**
     * The CupboardFileDAO object that allows the Controller to <br>
     * communicate with JSON files.
     */
    private final CupboardDAO cupboardDAO;

    /**
     * Logger object to help track Http requests in the terminal
     */
    private static final Logger LOG =
            Logger.getLogger(CupboardController.class.getName());

    /**
     * The CupboardController constructor creates a new CupboardFileDAO <br>
     * object to allow the Controller to communicate with the JSON files.
     */
    public CupboardController(CupboardDAO cupboardDAO) {

        this.cupboardDAO = cupboardDAO;
        LOG.info(
                "CupboardController initialized; CupboardDAO injected\n");

    }

    /**
     * The createNeed method takes in a Need from the body of an Http <br>
     * request and creates a new need in CupboardFileDAO given that <br>
     * the Need does not already exist and is not null.
     * @param need - Need to be added to CupboardFileDAO
     * @return - ResponseEntity with an Http status of CREATED if <br>
     *           the Need is successfully created, CONFLICT if the Need <br>
     *           already exists, and INTERNAL_SERVER_ERROR if an <br>
     *           IOException is thrown.
     */
    @PostMapping("/create")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {

        LOG.info("POST /ufundapi/create\n");

        // Make sure need doesn't already exist
        if (this.cupboardDAO.getAllNeeds().containsValue(need)) {

            LOG.info("\tPOST failed: Need already exists in Cupboard\n");
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        }

        // If the need doesn't already exist and it's not null, create it
        if (need != null) {

            if(this.cupboardDAO.createNeed(need) != null) {

                LOG.info("\tPOST succeeded: Need added to Cupboard\n\t\t"
                        + need.toString() + "\n");
                return new ResponseEntity<>(need, HttpStatus.CREATED);

            }else {

                LOG.info("\tPOST failed: Save failed\n");
                return new ResponseEntity<>(HttpStatus.CONFLICT);

            }

        } else {

            LOG.info("\tPOST failed: Need is null\n");
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        }

    }

    /**
     * Responds to the GET request for all {@linkplain Need needs}
     * 
     * @return ResponseEntity with Map of id to {@link Need need} objects (may be empty) and HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/get")
    public ResponseEntity<Need> getNeed(@RequestBody int id) {

        LOG.info("GET ufundapi/" + id + "\n");

        Need need = cupboardDAO.getNeed(id);

        if(need != null) {

            LOG.info("\t GET succeeded: " + need + "\n");
            return new ResponseEntity<Need>(need, HttpStatus.OK);

        }else {

            LOG.info("\tGET failed: Need not found in Cupboard\n");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

    }

    /**
     * Responds to get request for a single {@linkplain Need need} with a given id
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/needs")
    public ResponseEntity<Map<Integer, Need>> getAllNeeds() {

        LOG.info("GET /ufundapi/needs\n");

        Map<Integer, Need> needs = cupboardDAO.getAllNeeds();

        return new ResponseEntity<Map<Integer, Need>>(needs, HttpStatus.OK);

    }

    /**
     * Responds to the SEARCH request for all {@linkplain Need needs}
     * 
     * @return ResponseEntity with Map of id to {@link Need need} objects (may be empty) and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/")
    public ResponseEntity<Map<Integer, Need>>
                                        findNeeds(@RequestParam String name) {

        LOG.info("GET /ufundapi/" + name + "\n");

        Map<Integer, Need> needs = cupboardDAO.findNeeds(name);

        if(!needs.isEmpty()) {

            LOG.info(
                    "\tGET succeeded: Got " + needs.size() + " needs\n");
            return new
                    ResponseEntity<>(needs, HttpStatus.OK);

        }else {

            LOG.info(
                    "\tGET failed: There are no Needs in the cupboard\n");
            return new
                    ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }

    }

    /**
     * The filterPrice method handles the Http request for filtering <br>
     * the Needs in the Cupboard by the given price range.
     * @param priceRange - The price range, given in the Http request <br>
     *                     as a String in the format "lowPrice-highPrice"
     * @return - ResponseEntity<Map<Integer, Need>> the Http response <br>
     *           of the operation - EXPECTATION_FAILED if an invalid <br>
     *           price range is given, NOT_FOUND if no Needs are found <br>
     *           in the given price range, and OK if the operation succeeds.
     */
    @GetMapping("/filterPrice")
    public ResponseEntity<Map<Integer, Need>> filterPrice(
            @RequestBody double[] priceRange) {

        // Converting String parameter to two double parameters
        /*
        String[] fields = priceRange.split("-");
        double lowPrice;
        double highPrice;
        if(fields[0].isEmpty()) {

            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);

        }else {

            lowPrice = Double.parseDouble(fields[0]);
            highPrice = Double.parseDouble(fields[1]);

        }

        Map<Integer, Need> filtered =
                this.cupboardDAO.filterPrice(lowPrice, highPrice);
         */

        if(priceRange[0] < 0) {

            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);

        }

        Map<Integer, Need> filtered =
                this.cupboardDAO.filterPrice(priceRange[0], priceRange[1]);

        if(filtered != null) {

            return new ResponseEntity<>(filtered, HttpStatus.OK);

        }else {

            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }


    }

    /**
     * The filterTags method handles the Http request for filtering <br>
     * the Needs in the Cupboard by the String tags they are <br>
     * associated with.
     * @param tags - ArrayList <String> the String tags to filter <br>
     *               the Needs in the Cupboard by.
     * @return - ResponseEntity<Map<Integer, Need>> the result of the <br>
     *           operation - EXPECTATION_FAILED if tags is null or empty, <br>
     *           NOT_FOUND if no Needs are found with the given tags, <br>
     *           and OK if the operation succeeds.
     */

    @GetMapping("/filterTags")
    public ResponseEntity<Map<Integer, Need>> filterTags(
            @RequestBody ArrayList<String> tags) {

        if(tags == null || tags.isEmpty()) {

            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);

        }else {

            Map<Integer, Need> filtered = this.cupboardDAO.filterTags(tags);

            if(filtered != null) {

                return new ResponseEntity<>(filtered, HttpStatus.OK);

            }else {

                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            }

        }

    }

    /**
     * The filterNumberSold method handles the Http request for <br>
     * sorting the Needs in the Cupboard by the number sold (from <br>
     * high to low or low to high, as given by the parameter in the <br>
     * form "highlow" or "lowhigh").
     * @param highToLow - String denoting whether to sort the Cupboard <br>
     *                    from high to low ("highlow") or low to high <br>
     *                    ("lowhigh").
     * @return - ResponseEntity<ArrayList<Need> the Http response of the <br?
     *           operation - EXPECTATION_FAILED if the input String is <br>
     *           invalid, NOT_FOUND if no Needs are in the Cupboard, <br>
     *           and OK if the operation succeeds.
     */
    @GetMapping("/filterNumberSold")
    public ResponseEntity<ArrayList<Need>> filterNumberSold(
            @RequestBody Boolean highToLow) {

        ArrayList<Need> sorted = this.cupboardDAO.filterNumberSold(highToLow);

        /*
        if(highToLow) {

            sorted = this.cupboardDAO.filterNumberSold(true);

        }else {

            sorted = this.cupboardDAO.filterNumberSold(false);

        }
         */

        if(sorted == null || sorted.isEmpty()) {

            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }else {

            return new ResponseEntity<>(sorted, HttpStatus.OK);

        }

    }

    /**
     * ResponseEntity to handle DeleteNeed
     * @param id - int the id of the Need to delete
     * @return - ResponseEntity<Need> containing the HttpStatus of the <br>
     *           operation (OK if delete succeeds, NOT_FOUND if the <br>
     *           Need was not found).
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Need> deleteNeed(@PathVariable int id) {

        LOG.info("DELETE /ufundapi/" + id + "\n");

        if (this.cupboardDAO.deleteNeed(id) != null) {

            LOG.info("DELETE succeeded\n");
            return new ResponseEntity<>(HttpStatus.OK);

        } else {

            LOG.info("DELETE failed: Need not found in Cupboard\n");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }

}

    /**
     * Response Entity to update handling Need
     * @return - Returns HttpStatus 
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Need> updateNeed(@RequestBody Need need,
                                           @PathVariable int id) {

        LOG.info("PUT /ufundapi/update/" + id + "\n");

        if(need != null) {

            if (this.cupboardDAO.getNeed(id) != null) {

                Need updated = this.cupboardDAO.updateNeed(need, id);
                LOG.info("\tPUT succeeded: Need updated\n");
                return new ResponseEntity<>(updated, HttpStatus.OK);

            } else {

                LOG.info("\tPUT failed: " +
                        "Need does not exist in Cupboard\n");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            }

        }else {

            LOG.info("PUT failed: Need is null");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }

    }

    /**
     * The getAllTags function handles the HTTP request for getting all <br>
     * of the categorization tags used by the Need class.
     * @return - ResponseEntity<List<String>> the List of String tags and <br>
     *           an HTTP status of OK if the tags List does not <br>
     *           return null and an HTTP status of NOT_FOUND if the tags <br>
     *           List does return null.
     */
    @GetMapping("/allTags")
    public ResponseEntity<List<String>> getAllTags() {

        LOG.info("GET /ufundapi/tags\n");

        List<String> tags = this.cupboardDAO.getAllTags();

        if(tags != null) {

            return new ResponseEntity<>(tags, HttpStatus.OK);

        }else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

    }

}
