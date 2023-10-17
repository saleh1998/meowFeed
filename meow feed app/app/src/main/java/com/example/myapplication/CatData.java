package com.example.myapplication;

import java.util.Date;

public class CatData {

    private int id;
    private double eaten;
    private double dispensed;
    private double totalWeight;
    private Date date;

    public CatData(int id, double eaten, double dispensed, double totalWeight, Date date) {
        this.id = id;
        this.eaten = eaten;
        this.dispensed = dispensed;
        this.totalWeight = totalWeight;
        this.date = date;
    }

    public CatData(double dispensed, double totalWeight, Date date) {
        this.dispensed = dispensed;
        this.totalWeight = totalWeight;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getEaten() {
        return eaten;
    }

    public void setEaten(double eaten) {
        this.eaten = eaten;
    }

    public double getDispensed() {
        return dispensed;
    }

    public void setDispensed(double dispensed) {
        this.dispensed = dispensed;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}