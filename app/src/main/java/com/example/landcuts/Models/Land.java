package com.example.landcuts.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Land implements Serializable {
    String name, location;
    long initialPrice;
    long currentPrice;
    int no_of_available_cuts;
    String imageUri;
    ArrayList<User> users_who_bought_current_land;

    public Land(String name, String location, long initialPrice) {
        this.name = name;
        this.location = location;
        this.initialPrice = initialPrice;
        no_of_available_cuts = 100;
        users_who_bought_current_land = new ArrayList<>();
        this.currentPrice = initialPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(long initialPrice) {
        this.initialPrice = initialPrice;
    }

    public int getNo_of_available_cuts() {
        return no_of_available_cuts;
    }

    public void setNo_of_available_cuts(int no_of_available_cuts) {
        this.no_of_available_cuts = no_of_available_cuts;
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public ArrayList<User> getUsers_who_bought_current_land() {
        return users_who_bought_current_land;
    }

    public void setUsers_who_bought_current_land(ArrayList<User> users_who_bought_current_land) {
        this.users_who_bought_current_land = users_who_bought_current_land;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
