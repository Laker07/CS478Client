package com.example.tony.crypto.POJOS;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by tonyd on 3/29/2017.
 */

public class Friend {

    String name;
    String publicKey;
    String privateKey;
    public Friend(){}

    public Friend(String name, String publicKey, String privateKey){
        this.name = name;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public Friend(String name, String privateKey){
        this.name = name;
        this.publicKey = null;
        this.privateKey = privateKey;
    }

    public Friend(String name){
        this.name = name;
        this.publicKey = null;
        this.privateKey = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }


}
