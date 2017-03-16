package com.example.tony.crypto.EncDec;

/**
 * Anthony Martinez
 * CECS 478
 * Prof Dr. Aliasgari
 *

 */

public class Keys {
    String ivaes;
    String hmac;
    String rsa;
    Keys(String aes, String hmac, String rsa){
        this.ivaes = aes;
        this.hmac = hmac;
        this.rsa = rsa;
    }

    public String getIvaes(){return ivaes;}
    public String getHmac(){return hmac;}
    public String getRsa(){return rsa;}

}
