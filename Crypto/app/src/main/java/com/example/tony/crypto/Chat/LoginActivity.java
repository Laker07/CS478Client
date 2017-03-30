package com.example.tony.crypto.Chat;

/**
 * Created by tony on 3/18/17.
 */

/**
 * Anthony Martinez
 * Michael Munoz
 * CECS 478
 * Prof Dr. Aliasgari
 * Small android app to show/test enc/dec

 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tony.crypto.DB.DBHandler;
import com.example.tony.crypto.EncDec.Encrypt;
import com.example.tony.crypto.EncDec.Keys;
import com.example.tony.crypto.EncDec.RSAKeyGen;
import com.example.tony.crypto.POJOS.Friend;
import com.example.tony.crypto.POJOS.Register;
import com.example.tony.crypto.POJOS.User;
import com.example.tony.crypto.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //use "http://10.0.2.2:<port> for emulator volley request
    private static final String ENDPOINTSIGNIN = "http://10.0.2.2:8080/signIn";
    private static final String ENDPOINTREGISTER = "http://10.0.2.2:8080/register";
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String jwt = "jwt";

    EditText name;
    EditText pwds;
    Register reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        jwt = sharedPreferences.getString("jwt", "not set");

        name = (EditText) findViewById(R.id.username);
        pwds = (EditText) findViewById(R.id.password);
        Button submit = (Button)findViewById(R.id.buttonSubmit);
        Button register = (Button)findViewById(R.id.register);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //db

        String barr = "hello im' private";
        String carr = "hello i'm public";

        final DBHandler db = new DBHandler(this);
//        Log.d("Insert: ", "Inserting....");
//        db.addMe(new Friend("tony",carr.getBytes(), barr.getBytes()));
//        db.addMe(new Friend("arch", null, barr.getBytes()));
//
//        Log.d("Reading entries","reading......");
//        List<Friend> friends = db.getAllFriends();
//
//        for(Friend f: friends){
//            String log = "Name : " + f.getName();
//            byte[] a = f.getPrivateKey();
//
//            Log.d("Name: ", log );
//        }

        submit.setOnClickListener(new View.OnClickListener(){
            Context c = getApplicationContext();

            @Override
            public void onClick(View v){
                JsonObjectRequest reqPost = new JsonObjectRequest(Request.Method.POST, ENDPOINTSIGNIN, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                reg = gson.fromJson(response.toString(), Register.class);

                                if(reg.getResponse().equals("Error")){
                                    Log.d("if error: ." ,reg.getMessage());
                                    CharSequence text = reg.getMessage();
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(c, text,duration);
                                    toast.show();

                                }else{
                                    //log and verify
                                    Log.d("all", response.toString());
                                    Log.d("response : ", reg.getResponse());
                                    Log.d("msg      : ", reg.getMessage());
                                    Log.d("jwt      : ", reg.getJwt());
                                    //place jwt and username in preferences
                                    editor.putString("jwt", reg.getJwt());
                                    editor.putString("name", name.getText().toString());
                                    editor.commit();
                                    //switch screens
                                    Intent intent = new Intent(getApplicationContext(), MessageList.class);
                                    startActivity(intent);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //network error response
                        //more info at:
                        // https://github.com/mcxiaoke/android-volley/blob/master/src/main/java/com/android/volley/VolleyError.java
                        Log.i("error", "onErrorResponse");
                        error.printStackTrace();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return header();
                    }
                    @Override
                    public byte[] getBody() {
                       return body();
                    }
                };
                requestQueue.add(reqPost);
            }
        });


        register.setOnClickListener(new View.OnClickListener(){
            Context c = getApplicationContext();

            @Override
            public void onClick(View v){
                JsonObjectRequest reqPost = new JsonObjectRequest(Request.Method.POST, ENDPOINTREGISTER, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //create new object to hold json response
                                Gson gson = new Gson();
                                reg = gson.fromJson(response.toString(), Register.class);

                                //log and verify
                                Log.d("all", response.toString());
                                Log.d("response : ", reg.getResponse());
                                Log.d("msg      : ", reg.getMessage());
                                Log.d("jwt      : ", reg.getJwt());

                                //place jwt and username in preferences
                                editor.putString("jwt", reg.getJwt());
                                editor.putString("name", name.getText().toString());
                                editor.commit();

                                //db stuff
                                //add your name and key
                                //this needs to mode to another thread
                                //waits will key is generated and written to db
                                KeyPair key = RSAKeyGen.generate();
                                db.addMe(new Friend(name.getText().toString(), RSAKeyGen.getPublic(key), RSAKeyGen.getPrivate(key)));

                                Log.d("Reading entries","reading......");
                                Friend f = null;
                                try {
                                    f = db.getFriend(name.getText().toString());
                                } catch (InvalidKeySpecException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String log = "Name : " + f.getName() + "  public: " + f.getPublicKey() + " priv : " +f.getPrivateKey();
                                Log.d("Name: ", log );

                                //switch screens
                                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                startActivity(intent);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.e("Volley Error: ", error.getMessage());
                    }
                }){
                    //set header content type to application json
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return header();
                    }
                    //send json with (username, password) using Gson
                    @Override
                    public byte[] getBody() {
                       return body();
                    }
                };
                requestQueue.add(reqPost);
            }
        } );





    }//end onCreate

    public Map<String,String> header(){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
    public byte[] body(){
        try {
            //create User obj and convert to json string with gson
            User requestBodyClass = new User(name.getText().toString(), pwds.getText().toString());
            Gson gson = new Gson();
            final String requestBody = gson.toJson(requestBodyClass, User.class);
            return requestBody == null ? null : requestBody.getBytes("utf-8");

        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding");
            return null;
        }
    }
}
