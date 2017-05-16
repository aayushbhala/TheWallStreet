package com.example.android.thestreet;

/**
 * Created by MAHE on 14-May-17.
 */
public class SearchStock {
    private int id;
    private String name;
    private String industry;
    public SearchStock(int i,String n,String ind){
        id = i;
        name = n;
        industry = ind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
