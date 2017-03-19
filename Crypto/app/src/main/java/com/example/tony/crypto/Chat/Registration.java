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


//import com.example.tony.crypto.

public class Registration extends AppCompatActivity {

    //use "http://10.0.2.2:<port> for emulator volley request
    private static final String ENDPOINTR = "http://10.0.2.2:8081/register2";
    private RequestQueue requestQueue;
    TextView jsonI;
    String re = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String jwt = "jwt";

    EditText name;
    EditText pwds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        jwt = sharedPreferences.getString("jwt", "not set");
        Log.d("TOP PREF JWT : ",  jwt);

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
                                try {
                                    String respon = response.getString("response");

                                    //VolleyLog.v("Response:%n %s", response.toString(4));
                                    Log.d("all", response.toString());

                                    String msg = response.getString("message");
                                    String j = response.getString("jwt");
                                    Log.d("response : ", respon);
                                    Log.d("msg      : ", msg);
                                    Log.d("jwt      : ", j);
                                    editor.putString("jwt", j);
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                    startActivity(intent);

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
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("username", name.getText().toString());
                        parameters.put("password", pwds.getText().toString());
                        return parameters;
                    }
                    @Override
                    public byte[] getBody() {
                        try {
                            User requestBodyClass = new User(name.getText().toString(), pwds.getText().toString());//userName.getText().toString(), pwd.getText().toString());
                            Gson gson = new Gson();
                            final String requestBody = gson.toJson(requestBodyClass, User.class);


                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    null , "utf-8");
                            return null;
                        }
                    }
                };
                //RequestQueue rQueue = Volley.newRequestQueue(Registration.this);
                requestQueue.add(reqPost);
            }
        } );
    }//end onCreate


}
