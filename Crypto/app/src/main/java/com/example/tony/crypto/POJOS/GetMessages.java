package com.example.tony.crypto.POJOS;

/**
 * Created by tonyd on 4/2/2017.
 */

public class GetMessages {

    private String response;
    private int messagecount;
    private String messages;
    private String sender;
    private String timestamp;

    public GetMessages(String response, int messagecount, String messages) {
        this.response = response;
        this.messagecount = messagecount;
        this.messages = messages;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(int messagecount) {
        this.messagecount = messagecount;
    }

    public String getMessage() {
        return messages;
    }

    public void setMessage(String message) {
        this.messages = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
