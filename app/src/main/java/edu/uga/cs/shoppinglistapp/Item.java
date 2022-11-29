package edu.uga.cs.shoppinglistapp;

/** POJO or single object for single shopping item */
public class Item {
    private String name;
    private int amount;
    private double price;

    public Item () {
        this.name = null;
        this.amount = -1;
    }


    public Item(String itemName, int itemAmount) {
        this.name = itemName;
        this.amount = itemAmount;
    }

    public String getName() {return name;}

    public void setName(String itemName) {name = itemName;}

    public int getAmount() {return amount;}

    public void setAmount(int itemAmount) {amount = itemAmount;}

    public double getPrice() {return price;}

    public void setPrice(double itemPrice) {price = itemPrice;}
}
