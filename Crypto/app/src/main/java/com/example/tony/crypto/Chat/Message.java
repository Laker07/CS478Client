package com.example.tony.crypto.Chat;


import java.util.Date;

/**
 * Created by tony on 3/15/2017.
 */

public class Message {
    private String id;
    private String text;
    private String user;
    private Date date;
    private String jwt;


    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public Date getCreatedAt() {
        return date;
    }
}
