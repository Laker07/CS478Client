package com.example.tony.crypto.EncDec;

/**
 * Anthony Martinez
 * Michael Munoz
 * CECS 478
 * Prof Dr. Aliasgari
 * Class Encrypt: has two methods Enc and Dec

 */

import android.content.Context;
import android.content.res.AssetManager;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.io.pem.PemReader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import java.security.*;

import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import com.google.gson.*;


public class Encrypt {

    //Will use BouncyCastle as the provider
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String Enc(String test, Context context){
        /*create IV
          1-Create random
          2-Create a byte[] of the size of the IV
          3-Get next random
          4-Create the IV by passing in your byte[] into IvParameterSpec
        */
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        byte[] aesPlusIV;
        String aesIVString="";
        String rsaK="";
        String hmacHex="";

        try {

            /*
             "SC" spongy castle, wrapper for bouncy castle
             Generate an AES key with size of 256.
             Create a cipher using AES/CBC/PKCS7Padding.
             Set cipher to ENCRYPT_MODE and pass in key, and IV.
            */
            KeyGenerator keyGen = KeyGenerator.getInstance("AES", "SC");
            keyGen.init(256);
            SecretKey aesKey = keyGen.generateKey();
            Cipher encrypt = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC");
            encrypt.init(Cipher.ENCRYPT_MODE, aesKey, iv);
            //encrypt text and pass to byte[]
            byte [] aesCipher = encrypt.doFinal(test.getBytes());
            //stream to concat iv and aesCipher
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            outStream.write(iv.getIV());
            outStream.write(aesCipher);
            outStream.close();
            //convert to send in json
            aesPlusIV = outStream.toByteArray();
            aesIVString = Hex.toHexString(aesPlusIV);

            /* Test decrypt
            byte[] t = Arrays.copyOfRange(aesPlusIV, 16, aesPlusIV.length);
            //test decrypt
            Cipher dec = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC");
            dec.init(Cipher.DECRYPT_MODE, aesKey, iv);
            byte[] decMsg =dec.doFinal(t);
            System.out.println("this is my msg: " + new String(decMsg));
            */

            /*Hmac
              1-Generate a Hmac key with SHA256
              2-Generate Hmac and init with SHA256 key created
              3-hmac.doFinal on concat iv+aes
             */
            //1
            KeyGenerator hmacKeyGenerator = KeyGenerator.getInstance("HMacSHA256", "SC");
            hmacKeyGenerator.init(256);
            SecretKey hKey = hmacKeyGenerator.generateKey();
            // 2- hmac
            Mac hmac = Mac.getInstance("HMacSHA256", "SC");
            hmac.init(hKey);
            //3
            byte[] hmacDat = hmac.doFinal(aesPlusIV);
            hmacHex = Hex.toHexString(hmacDat);
            System.out.println("Enc hmac : "+ hmacHex);

            /*RSA
              Get public key from directory
              Parse pem
              Get pem bytes
              Convert bytes to X509EncodedKeySpec
              Get cipher with RSA/NONE/OAEPPadding
              Encrypt, pass in a generated key from KeyFactory
             */

            //path will need to change in use
            AssetManager manager = context.getAssets();

            //String keyPath = "C:/public_key.pem"; //get pem
            //PemReader r = new PEMParser(new FileReader(keyPath)); //read in
            InputStreamReader rs = new InputStreamReader(manager.open("keys/public_key.pem"));

            //get string from db
            //PemReader r = new PEMReader(new StringReader(keystring);
            PemReader r = new PEMParser(rs);

            byte[] pubkey = r.readPemObject().getContent(); //get bytes
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(pubkey); //encode
            KeyFactory keyGenRSA = KeyFactory.getInstance("RSA", "SC"); //sets to rsa key
            Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "SC");
            c.init(Cipher.ENCRYPT_MODE, keyGenRSA.generatePublic(x509));

            //concatenate aes key and hmac key
            outStream = new ByteArrayOutputStream();
            outStream.write(aesKey.getEncoded());
            outStream.write(hKey.getEncoded());
            outStream.close();
            //encrypt concatenated keys
            byte[] rsaC = c.doFinal(outStream.toByteArray());
            rsaK = Hex.toHexString(rsaC); //get hex string

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            //e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Keys is a POJO, Gson is a json library
          Using Gson to convert Keys into json
         */
        Keys gsonK = new Keys(aesIVString, hmacHex, rsaK);
        Gson gson = new Gson();
        return gson.toJson(gsonK, Keys.class);
    }

    /**
     *
     * @param jObj is a json object that is retrieved with Gson
     */
    public String Dec(String jObj, Context context)  {

        String message="";//message to return
        Gson gson = new Gson();
        Keys data = gson.fromJson(jObj, Keys.class);

        String keyPath = "C:/private_key.pem"; //get pem

        try {
            /*retrieve json from jObj
             1-Read in private key
             2-Create cipher to decrypt
             3-Decrypt
             4-Separate aes and hmac keys
             */
            //1-
            AssetManager manager = context.getAssets();

            InputStreamReader rs = new InputStreamReader(manager.open("keys/private_key.pem"));
            PemReader r = new PEMParser(rs);
            byte[] pubkey = r.readPemObject().getContent(); //get bytes


            //maybe get key from db and place directly in

            PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(pubkey); //encode
            KeyFactory rsaKey = KeyFactory.getInstance("RSA","SC");
            //2- pass in recovered rsa key
            Cipher c = Cipher.getInstance("RSA/NONE/OAEPPadding", "SC");
            c.init(Cipher.DECRYPT_MODE,rsaKey.generatePrivate(pkcs));
            //3 - pass in to byte array
            byte[] savedKeys = c.doFinal(Hex.decode(data.rsa));
            //4-
            byte[] aesK = Arrays.copyOfRange(savedKeys, 0, savedKeys.length/2);
            byte[] hmacK = Arrays.copyOfRange(savedKeys, savedKeys.length/2, savedKeys.length);



            /*hmac
              1-Get aes and iv in aes
              2-craet hmac
              3-retrieve hmac key
              4-encrypt ivaes
             */
            //-1
            byte[] aesIv = Hex.decode(data.ivaes);
            //2-
            Mac hmac = Mac.getInstance("HMacSHA256", "SC");
            //3
            SecretKey hKey = new SecretKeySpec(hmacK, 0, hmacK.length, "HMacSHA256");
            hmac.init(hKey);
            //4
            byte[] hmacDat = hmac.doFinal(aesIv);
            String hmacHexString = Hex.toHexString(hmacDat);

            System.out.println("Old hmac: " + data.hmac);
            System.out.println("New hmac: " + hmacHexString);
            System.out.println("RSA : " + data.rsa);
            //check if hmac sent in and hmac recreated match
            //if they match proceed, else do nothing
            if(data.hmac.equalsIgnoreCase(hmacHexString)){
                //split to iv and cipher
                //decode aes with this iv and cipher to decode
                byte[] ivBytes = Arrays.copyOfRange(aesIv, 0, 16);
                byte[] msg = Arrays.copyOfRange(aesIv,16, aesIv.length);

                KeyGenerator keyGen = KeyGenerator.getInstance("AES", "SC");
                keyGen.init(256);
                SecretKey k = new SecretKeySpec(aesK,0,aesK.length,"AES");
                //SecretKey k = keyGen.getAlgorithm("AES");
                Cipher encrypt = Cipher.getInstance("AES/CBC/PKCS7Padding", "SC");

                IvParameterSpec iv = new IvParameterSpec(ivBytes);
                encrypt.init(Cipher.DECRYPT_MODE, k, iv);
                byte[] wow= encrypt.doFinal(msg, 0, msg.length);
                message = new String(wow);
                //System.out.println("WTF.............." + new String(wow));

            }else{
                //message for debuging
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            //e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            //e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return message;
    }
//    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException {
//        Encrypt enc = new Encrypt();
//
//        String result = enc.Dec(enc.Enc("this took for ever..............."));
//        System.out.println(result);
//    }




}
