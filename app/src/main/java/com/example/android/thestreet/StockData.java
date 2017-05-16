package com.example.android.thestreet;

/**
 * Created by MAHE on 13-May-17.
 */
public class StockData {
    private String name;
    private int stockID;
    private double purchasePrice;
    private double currentPrice;
    private int volume;
    private String date;
    private double change;

    public StockData(String n,int id,double pp,double cp,int v,String d,double ch){
        name = n;
        stockID = id;
        purchasePrice = pp;
        currentPrice = cp;
        volume = v;
        date = d;
        change = ch;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}
