package com.ufund.api.ufundapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The BasketController class contains the methods necessary to <br>
 * handle HTTP requests regarding modifying the items in a User's <br>
 * Basket. It is dependent on the UserFileDAO class for persistence and <br>
 * the Basket class for the backend Basket functionality.
 *
 * @author Benjamin Bremer
 */
@RestController
@RequestMapping("basket")
public class BasketController {

    /**
     * BasketController uses the UserFileDAO for persistence <br>
     * BasketController handles the Http requests for a Basket which is <br>
     * held by a user.
     */
    private final UserDAO userFileDAO;

    private final CupboardDAO cupboardFileDAO;

    /**
     * Logger to help keep track of Http requests in the command line
     */
    private static final Logger LOG =
            Logger.getLogger(BasketController.class.getName());

    /**
     * Constructor has a UserFileDAO object injected into it.
     */
    public BasketController(UserDAO userFileDAO, CupboardDAO cupboardFileDAO) {

        this.userFileDAO = userFileDAO;
        this.cupboardFileDAO = cupboardFileDAO;
        LOG.info("Initialized BasketController; UserFileDAO\n");

    }

    /**
     * The addNeedBasket method takes in a username (that corresponds <br>
     * to a basket) and the id of a Need to be added to that User's Basket. <br>
     * It handles the IOException that might be thrown by UserFileDAO <br>
     * and the OutOfStockException that might be thrown by Basket.
     * @param username - String the username of the User whose Basket <br>
     *                   we want to modify.
     * @param id - int the id of the Need to be added to the User's Basket
     * @return - ResponseEntity<HttpStatus> indicating the status of the
     *           operation after execution (INTERNAL_SERVER_ERROR <br>
     *           if an IOException is thrown, EXPECTATION_FAILED if <br>
     *           the item does not have enough stock, and OK if <br>
     *           the operation succeeds).
     */
    @PutMapping("/{username}/{id}")
    public ResponseEntity<Basket> addNeedBasket (
            @PathVariable("username") String username,
            @PathVariable("id") int id) {

        LOG.info("PUT /ufundapi/" + username + "\n");

        try {

            Basket basket;

            if((basket = this.userFileDAO.getBasket(username)) == null) {

                LOG.info("\tPUT failed: Basket does not ezist\n");
                return new ResponseEntity<>(basket, HttpStatus.NOT_FOUND);

            }else {

                Need need;

                if((need = this.cupboardFileDAO.getNeed(id)) != null) {

                    this.userFileDAO.updateBasket(
                            username, this.userFileDAO.getBasket(username).
                                    addNeedBasket(need));
                    LOG.info("\tPUT succeeded: " +
                            "Need added to User's Basket\n");
                    return new ResponseEntity<>(basket, HttpStatus.OK);

                }else {

                    LOG.info("\tPUT failed: Need is null\n");
                    return new ResponseEntity<>(null,
                            HttpStatus.INTERNAL_SERVER_ERROR);

                }

            }

        }catch (IOException ioException) {

            LOG.info("\tPUT failed: IOException\n");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (OutOfStockException outOfStockException) {

            LOG.info("\tPUT failed: OutOfStockException\n");
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);

        }

        /*
        try {
            if(this.userFileDAO.getBasket(username) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            this.userFileDAO.updateBasket(username,
                    this.userFileDAO.getBasket(username).addNeedBasket(need));
            LOG.info("\tPUT succeeded: Need added to User's Basket\n");
            return new ResponseEntity<>(HttpStatus.OK);

        }catch (IOException ioException) {

            LOG.info("\tPUT failed: IOException\n");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (OutOfStockException outOfStockException) {

            LOG.info("\tPUT failed: Item is out of stock\n");
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        }

         */

    }

    /**
     * The removeNeedBasket method takes in a username (that corresponds <br>
     * to a basket) and a Need to be removed from that User's Basket. <br>
     * It handles the IOException that might be thrown by UserFileDAO.
     * @param username - String the username of the User whose Basket <br>
     *                   we want to remove an item from.
     * @param id - int the id of the Need to be removed from the User's
     *             Basket.
     * @return - ResponseEntity<HttpStatus> the status of the operation <br>
     *           after execution (INTERNAL_SERVER_ERROR if an IOException <br>
     *           is thrown and OK if the operation succeeds).
     */
    @DeleteMapping("/{username}/{id}")
    public ResponseEntity<Basket> removeNeedBasket(
            @PathVariable("username") String username,
            @PathVariable("id") int id) {

        LOG.info("DELETE /ufundapi/" + username + "\n");

        try {

            Basket basket = this.userFileDAO.getBasket(username);

            this.userFileDAO.updateBasket(username,
                    basket.removeNeedBasket(id));
            LOG.info("\tDELETE succeeded: " +
                    "Need removed from User's Basket\n");
            return new ResponseEntity<>(basket, HttpStatus.OK);

        }catch (IOException ioException) {

            LOG.info("\tDELETE failed: IOException\n");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (NullPointerException nullPointerException) {

            LOG.info("\tDELETE failed: NullPointerException\n");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    /**
     * The fundBasket method funds a specified User's Basket. It handles <br>
     * the IOException that might be thrown by UserFileDAO and the <br>
     * OutOfStockException that might be thrown by Basket.
     * @param username - String the username of the User whose Basket <br>
     *                   needs to be funded.
     * @return - ResponseEntity<HttpStatus> the status of the operation <br>
     *           after execution (INTERNAL_SERVER_ERROR if an IOException <br>
     *           is thrown, EXPECTATION_FAILED if an OutOfStockException <br>
     *           is thrown, and OK if the operation succeeds).
     */
    @PutMapping("/{username}/checkout")
    public ResponseEntity<Basket> fundBasket(
            @PathVariable String username) {

        LOG.info("PUT /ufundapi/" + username + "/checkout\n");

        try {

            Basket basket = this.userFileDAO.getBasket(username);

            this.userFileDAO.updateBasket(username, basket.fundBasket());
            LOG.info("\tPUT succeeded: Needs checked out\n");
            return new ResponseEntity<>(basket, HttpStatus.OK);

        }catch (IOException ioException) {

            LOG.info("\tPUT failed: IOException\n");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (OutOfStockException outOfStockException) {

            LOG.info("\tPUT failed: Item(s) is(are) out of stock\n");
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);

        }

    }

    /**
     * The getBasket method gets the Basket associated with a User.
     * @param username - The username of the User the Basket belongs to
     * @return - The Basket of the specified User (null if User does not exist)
     */
    @GetMapping("/{username}")
    public ResponseEntity<Basket> getBasket(
            @PathVariable String username) {

        LOG.info("GET /" + username + "\n");

        try {

            Basket result = this.userFileDAO.getBasket(username);

            if(result != null) {

                LOG.info("\tGET succeeded\n");
                return new ResponseEntity<>(result, HttpStatus.OK);

            }else {

                LOG.info("\tGET failed: User does not exist\n");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

            }

        }catch (IOException ioException) {

            LOG.info("\tGET failed: IOException\n");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

}
