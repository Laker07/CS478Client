package com.example.tony.crypto.Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.Volley;
import com.example.tony.crypto.POJOS.User;
import com.example.tony.crypto.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Messenger extends AppCompatActivity {

    private static final String ENDPOINTR = "http://10.0.2.2:8081/sendMessage";
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Button submit = (Button)findViewById(R.id.submitMessage);
        TextView msg = (TextView) findViewById(R.id.inputMessage);
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        submit.setOnClickListener(new View.OnClickListener(){


            Context c = getApplicationContext();
            @Override
            public void onClick(View v) {

                JsonObjectRequest reqPost = new JsonObjectRequest(Request.Method.POST, ENDPOINTR, null,
                        new Response.Listener<JSONObject>() {


                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //verify 200
                                    String respon = response.getString("response");

                                    //VolleyLog.v("Response:%n %s", response.toString(4));
                                    Log.d("all", response.toString());

                                    String msg = response.getString("message");

                                    Log.d("response : ", respon);

                                } catch (JSONException e) {
                                    Log.d("JSONException msg: ", e.getMessage());
                                    Context context = getApplicationContext();
                                    CharSequence text = "Invalid user name!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String jwtToken = sharedPreferences.getString("jwt", null);
                        Log.d("JWT AT HEADER: ", jwtToken);
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + jwtToken);
                        return headers;
                    }

                    // @Override

                    //@Override
                    //       public byte[] getBody() {
                    //  try {
                    //
                        /*
                            private String id;
                            private String text;
                            private String user;
                            private Date date;
                            private String jwt;
                            */

                    //Message ---- new Message(
                    //Gson gson = new Gson();

//                            User requestBodyClass = new User(name.getText().toString(), pwds.getText().toString());//userName.getText().toString(), pwd.getText().toString());
//                            Gson gson = new Gson();
//                            final String requestBody = gson.toJson(requestBodyClass, User.class);
//
//
//                            return requestBody == null ? null : requestBody.getBytes("utf-8");
//                        } catch (UnsupportedEncodingException uee) {
//                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
//                                    null , "utf-8");
//                            return null;
//                        }
                    // }
                };


                RequestQueue rQueue = Volley.newRequestQueue(Messenger.this);
                requestQueue.add(reqPost);
            }
        } );




    }

}