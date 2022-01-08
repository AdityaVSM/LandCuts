package com.example.landcuts.Models;

public class Transaction extends Land{
    String boughtBy;
    long initial_buy_price;
    int no_of_shares_bought;

    public Transaction(){
        super();
    }

    public Transaction(String name, String location, long initialPrice) {
        super(name, location, initialPrice);
    }

    public String getBoughtBy() {
        return boughtBy;
    }

    public void setBoughtBy(String boughtBy) {
        this.boughtBy = boughtBy;
    }

    public long getinitial_buy_price() {
        return initial_buy_price;
    }

    public void setinitial_buy_price(long initial_buy_price) {
        this.initial_buy_price = initial_buy_price;
    }

    public int getNo_of_shares_bought() {
        return no_of_shares_bought;
    }

    public void setNo_of_shares_bought(int no_of_shares) {
        this.no_of_shares_bought = no_of_shares;
    }
}
