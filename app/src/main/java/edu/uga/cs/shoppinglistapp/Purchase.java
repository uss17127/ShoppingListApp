package edu.uga.cs.shoppinglistapp;

import java.util.ArrayList;
import java.util.List;

/** POJO or single object for single shopping item */
public class Purchase {
    private String key;
    private String buyer;
    private List<Item> items;

    public Purchase() {
        this.key = null;
        this.buyer = null;
        this.items = new ArrayList<Item>();
    }


    public Purchase(String buyerPerson, List<Item> itemsBought) {
        this.key = null;
        this.buyer = buyerPerson;
        this.items = itemsBought;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyerPerson) {
        buyer = buyerPerson;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> itemsBought) {
        items = itemsBought;
    }

    public double getTotal() {
        double total = 0;
        for (Item x : items) {
            total += x.getPrice();
        }
        return total;
    }




}
