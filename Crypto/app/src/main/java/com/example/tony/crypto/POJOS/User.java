package com.example.tony.crypto.POJOS;

/**
 * Created by tony on 3/17/17.
 */

public class User {

    private String username;
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public User(){

    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPwd() {
        return password;
    }

    public void setPwd(String password) {
        this.password = password;
    }
}
