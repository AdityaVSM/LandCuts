package com.example.landcuts.Models;

public class Transaction extends Land{
    String boughtBy;
    long total_invested;
    int no_of_shares_bought;

    public Transaction(){
        super();
    }

    public Transaction(String boughtBy) {
        this.boughtBy = boughtBy;
        this.total_invested = super.currentPrice;
        this.no_of_shares_bought = 0;
    }

    public String getBoughtBy() {
        return boughtBy;
    }

    public void setBoughtBy(String boughtBy) {
        this.boughtBy = boughtBy;
    }

    public long gettotal_invested() {
        return total_invested;
    }

    public void settotal_invested(long total_invested) {
        this.total_invested = total_invested;
    }

    public int getNo_of_shares_bought() {
        return no_of_shares_bought;
    }

    public void setNo_of_shares_bought(int no_of_shares) {
        this.no_of_shares_bought = no_of_shares;
    }
}
