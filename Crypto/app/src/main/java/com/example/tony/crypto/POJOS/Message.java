package com.example.tony.crypto.POJOS;


import java.util.Date;

/**
 * Created by tony on 3/15/2017.
 */

public class Message {
    private String username;
    private String receiverName;
    private String message;
    private String date;

    //
    private String sender;

    public Message(String username, String receiverName, String message, String date) {
        this.username = username;
        this.receiverName = receiverName;
        this.message = message;
        this.date = date;
    }

    public Message(String message,String sender, String date){
        this.message = message;
        this.sender = sender;
        this.date = date;
    }
}
