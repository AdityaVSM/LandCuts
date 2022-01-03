package com.example.landcuts.Models;

public class Land {
    String name, location;
    long price;
    int no_of_available_cuts;

    public Land(String name, String location, long price) {
        this.name = name;
        this.location = location;
        this.price = price;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getNo_of_available_cuts() {
        return no_of_available_cuts;
    }

    public void setNo_of_available_cuts(int no_of_available_cuts) {
        this.no_of_available_cuts = no_of_available_cuts;
    }
}
