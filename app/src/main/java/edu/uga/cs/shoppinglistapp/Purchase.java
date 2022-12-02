package edu.uga.cs.shoppinglistapp;

import java.util.ArrayList;
import java.util.List;

/** POJO or single object for single shopping item */
public class Purchase {
    private String key;
    private String buyer;
    private List<Item> items;
    private List<Double> prices;
    private List<Integer> amounts;

    public Purchase() {
        this.key = null;
        this.buyer = null;
        this.items = new ArrayList<Item>();
        this.prices = new ArrayList<Double>();
        this.amounts = new ArrayList<Integer>();
    }


    public Purchase(String buyerPerson, List<Item> itemsBought, List<Double> itemPrices, List<Integer> itemAmounts) {
        this.key = null;
        this.buyer = buyerPerson;
        this.items = itemsBought;
        this.prices = itemPrices;
        this.amounts = itemAmounts;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBuyer() {return buyer;}

    public void setBuyer(String buyerPerson) {buyer = buyerPerson;}

    public List<Item> getItems() {return items;}

    public void setItems(List<Item> itemsBought) {items = itemsBought;}

    public List<Double> getPrices() {return prices;}

    public void setPrices(List<Double> itemPrices) {prices = itemPrices;}

    public List<Integer> getAmounts() {return amounts;}

    public void setAmounts(List<Integer> itemAmounts) {amounts = itemAmounts;}


}
