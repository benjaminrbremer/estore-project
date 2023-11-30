package com.ufund.api.ufundapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * The Basket class contains all necessary data and methods for one <br>
 * user's (helper's) basket.
 *
 * @author Benjamin Bremer (bwb1113)
 */
public class Basket {

    /**
     * basketItems keeps track of the Needs in the basket (keyed by Need ID)
     */
    @JsonProperty("basketItems") private Map<Integer, Need> basketItems;

    /**
     * basketQuantity keeps track of the number of Needs in the basket <br>
     * (keyed by Need)
     */
    @JsonProperty("basketQuantity") private Map<Need, Integer> basketQuantity;

    /**
     * The Basket constructor initializes the basketItems and <br>
     * basketQuantity HashMaps
     */
    public Basket() {

        this(new HashMap<Integer, Need>(), new HashMap<Need, Integer>());

    }

    public Basket(@JsonProperty("basketItems") Map<Integer, Need> basketItems, @JsonProperty("basketQuantity") Map<Need, Integer> basketQuantity) {

        this.basketItems = basketItems;
        this.basketQuantity = basketQuantity;

    }

    /**
     * The addNeedBasket method takes in a Need and does a couple <br>
     * different things in different cases: <br>
     * 1. In the case that this is the first time the Need is being added <br>
     *    to the basket, the Need gets added to basketItems and <br>
     *    basketQuantity with a quantity of 1. <br>
     * 2. In the case that the need has already been added to the basket <br>
     *    before (quantity >= 1), basketItems is not modified and <br>
     *    basketQuantity's value for that Need increases by 1 <br>
     *    (quantity of that Need in the basket increases by 1)
     * @param need - Need the Need to be added to the basket
     */
    public Basket addNeedBasket(Need need) throws OutOfStockException{

        if(!this.basketItems.containsKey(need.getProductID())) {

            if(need.getStock() > 0) {

                // If the Need is not already in the basket, add it to both
                // basketItems and basketQuantity with a quantity of 1
                this.basketItems.put(need.getProductID(), need);
                this.basketQuantity.put(need, 1);

            }else {

                // If there is not enough stock of that Need, throw an
                // OutOfStockException
                throw new OutOfStockException(
                        need.getName() + " is out of stock.");

            }

        }else {

            if(need.getStock() > this.basketQuantity.get(need)) {

                // If the Need is already in the basket (quantity >=1),
                // increase the value of basketQuantity at that Need by 1
                // (increase the quantity of that Need by 1)
                this.basketQuantity.put(need, this.basketQuantity.get(need) + 1);

            }else {

                // If there is not enough stock of that Need, throw an
                // OutOfStockException
                throw new OutOfStockException(
                        need.getName() + " does not have enough stock.");

            }

        }

        return this;

    }

    /**
     * The removeNeedBasket method takes in a Need and does a couple <br>
     * different things in different cases: <br>
     * 1. In the case that there is only 1 of the specified Need in the <br>
     *    basket, the Need is removed from both basketItems and <br>
     *    basketQuantity <br>
     * 2. In the case that there is more than 1 of the specified Need <br>
     *    in the basket, basketItems is left unmodified and the value <br>
     *    of basketQuantity at that need is decreased by 1 (the quantity <br>
     *    of that Need in the basket is decreased by 1). <br>
     * @param id - int the id of the Need to be removed from the Basket
     */
    public Basket removeNeedBasket(int id) {

        if(this.basketQuantity.get(this.basketItems.get(id)) == 1) {

            // In the case that there is only 1 of the Need in the basket,
            // remove it from basketItems and basketQuantity
            this.basketQuantity.remove(this.basketItems.get(id));
            this.basketItems.remove(id);


        }else {

            // In the case that there is more than 1 of the Need in the
            // basket, only decrease the quantity of the Need in the
            // basket by 1
            this.basketQuantity.put(this.basketItems.get(id),
                    this.basketQuantity.get(this.basketItems.get(id)) - 1);

        }

        return this;

    }

    /**
     * THe fundBasket method decreases the stock of each need in <br>
     * basketItems by the quantity of that Need in the basket <br>
     * as specified in basketQuantity. Following the update, both
     * basketItems and basketQuantity are cleared to simulate that <br>
     * the Helper "checked out".
     */
    public Basket fundBasket() throws OutOfStockException {

        // Update the stock of every Need in basketItems based on the
        // quantity of each need in the basket
        for(int key : this.basketItems.keySet()) {

            if(this.basketQuantity.get(this.basketItems.get(key)) <=
                    this.basketItems.get(key).getStock()) {

                this.basketItems.get(key).
                        updateStock(
                                this.basketItems.get(key).getStock() -
                                        this.basketQuantity.get(
                                                this.basketItems.get(key)));

            }else {

                throw new OutOfStockException(
                        this.basketItems.get(key).getName() +
                                " does not have enough stock to check out.");

            }

        }

        // Clear basketItems and basketQuantity simulating "check out"
        this.basketItems.clear();
        this.basketQuantity.clear();

        return this;

    }

    /**
     * The getBasketItems method returns the basketItems HashMap
     * @return - HashMap (Map) the basketItems HashMap
     */
    public Map<Integer, Need> getBasketItems() {

        return this.basketItems;

    }

    /**
     * The getBasketQuantity returns the basketQuantity HashMap
     * @return - HashMap (Map) the basketQuantity HashMap
     */
    public Map<Need, Integer> getBasketQuantity() {

        return this.basketQuantity;

    }

}
