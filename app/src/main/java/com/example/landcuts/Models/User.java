package com.example.landcuts.Models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    String name;
    String uid;
    String email;
    long currentBalance;
    long invested;
    ArrayList<Land> land_bought_by_current_user;

    public User() {
    }

    public User(String name, String uid, String email) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        currentBalance = 0;
        invested = 0;
        land_bought_by_current_user = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        this.currentBalance = currentBalance;
    }

    public long getInvested() {
        return invested;
    }

    public void setInvested(long invested) {
        this.invested = invested;
    }

    public ArrayList<Land> getLand_bought_by_current_user() {
        return land_bought_by_current_user;
    }

    public void setLand_bought_by_current_user(ArrayList<Land> land_bought_by_current_user) {
        this.land_bought_by_current_user = land_bought_by_current_user;
    }
}
