package com.example.tony.crypto.POJOS;

/**
 * Created by tonyd on 3/30/2017.
 */

public class ServerResponse {

    private String response;
    private String message;

    public ServerResponse(String response, String message) {
        this.response = response;
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
