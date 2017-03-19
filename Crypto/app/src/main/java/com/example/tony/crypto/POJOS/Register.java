package com.example.tony.crypto.POJOS;

/**
 * Created by tony on 3/17/17.
 */

public class Register {

    String response;
    String message;
    String jwt;

    public Register(String response, String message, String jwt) {
        this.response = response;
        this.message = message;
        this.jwt = jwt;
    }

    public String getJwt(){return jwt;}
}
