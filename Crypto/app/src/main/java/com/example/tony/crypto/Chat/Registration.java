package com.example.tony.crypto.Chat;

/**
 * Created by tony on 3/18/17.
 */

/**
 * Anthony Martinez
 * CECS 478
 * Prof Dr. Aliasgari
 * Small android app to show/test enc/dec

 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tony.crypto.EncDec.Encrypt;
import com.example.tony.crypto.EncDec.Keys;
import com.example.tony.crypto.POJOS.Register;
import com.example.tony.crypto.POJOS.User;
import com.example.tony.crypto.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    //use "http://10.0.2.2:<port> for emulator volley request
    private static final String ENDPOINTR = "http://10.0.2.2:8080/register";
    private RequestQueue requestQueue;
//    TextView jsonI;
//    String re = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String jwt = "jwt";

    Register reg;

    EditText name;
    EditText pwds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
//        jwt = sharedPreferences.getString("jwt", "not set");
//        Log.d("TOP PREF JWT : ",  jwt);

        name = (EditText) findViewById(R.id.username);
        pwds = (EditText) findViewById(R.id.password);
        Button submit = (Button)findViewById(R.id.buttonSubmit);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener(){
            Context c = getApplicationContext();

            @Override
            public void onClick(View v){
                JsonObjectRequest reqPost = new JsonObjectRequest(Request.Method.POST, ENDPOINTR, null,
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
                                //switch screens
                                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                startActivity(intent);

                                //decide where data validation should go
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
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                      //map version
//                    //set params to send the server (username, password)
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("username", name.getText().toString());
//                        parameters.put("password", pwds.getText().toString());
//                        return parameters;
//                    }
                    //send json with (username, password) using Gson
                    @Override
                    public byte[] getBody() {
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
                };
                requestQueue.add(reqPost);
            }
        } );
    }//end onCreate



}
