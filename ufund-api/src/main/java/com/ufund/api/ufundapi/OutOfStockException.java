package com.ufund.api.ufundapi;

/**
 * The OutOfStockException is thrown by the Basket class when a User <br>
 * attempts to add more Needs to their Basket than there are in stock <br>
 * and when a User attempts to check out their Basket when there are <br>
 * not enough Needs in stock.
 *
 * @author Benjamin Bremer (bwb1113)
 */
public class OutOfStockException extends Exception {

    /**
     * The OutOfStockException constructor takes in a message and passes <br>
     * it to its super constructor in Exception.
     * @param message - String the error message.
     */
    public OutOfStockException(String message) {

        super(message);

    }

    /**
     * The toString for the OutOfStockException just prints that a <br>
     * generic out of stock message.
     * @return - String generic out of stock message
     */
    /*
    @Override
    public String toString() {

        return "Item is out of stock.";

    }
     */

}
