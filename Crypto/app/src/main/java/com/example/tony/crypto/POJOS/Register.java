package com.example.tony.crypto.POJOS;

/**
 * Created by tony on 3/17/17.
 */

public class Register {

    String response;
    String message;
    String jwtToken;

    public Register(String response, String message, String jwt) {
        this.response = response;
        this.message = message;
        this.jwtToken = jwt;
    }

    public String getJwt(){return jwtToken;}
    public String getResponse(){return response;}
    public String getMessage(){return message;}

}
