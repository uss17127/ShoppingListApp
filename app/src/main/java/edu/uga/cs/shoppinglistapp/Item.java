package edu.uga.cs.shoppinglistapp;

/** POJO or single object for single shopping item */
public class Item {
    private long id;
    private String name;
    private int amount;
    private double price;

    public Item () {
        this.id = -1;
        this.name = null;
        this.amount = -1;
        this.price = -1;
    }


    public Item(String itemName, int itemAmount, double itemPrice) {
        this.id = -1;
        this.name = itemName;
        this.amount = itemAmount;
        this.price = itemPrice;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String itemName) {name = itemName;}

    public int getAmount() {return amount;}

    public void setAmount(int itemAmount) {amount = itemAmount;}

    public double getPrice() {return price;}

    public void setPrice(double itemPrice) {price = itemPrice;}
}
