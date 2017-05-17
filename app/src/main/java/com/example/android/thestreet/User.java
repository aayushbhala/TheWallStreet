package com.example.android.thestreet;

/**
 * Created by MAHE on 17-May-17.
 */
public class User {
    private String user_name;
    private int user_id;
    public User(int a,String n){
        user_name = n;
        user_id = a;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
