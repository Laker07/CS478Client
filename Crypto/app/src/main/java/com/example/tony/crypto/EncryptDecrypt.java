
/**
 * Anthony Martinez
 * CECS 478
 * Prof Dr. Aliasgari
 * Small android app to show/test enc/dec

 */

package com.example.tony.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.tony.crypto.EncDec.*;
import com.example.tony.crypto.POJOS.Register;
import com.example.tony.crypto.POJOS.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
//import com.example.tony.crypto.

public class EncryptDecrypt extends AppCompatActivity {

    //use "http://10.0.2.2:<port> for emulator volley request
    private static final String ENDPOINT = "http://10.0.2.2:8081/";
    private static final String ENDPOINTR = "http://10.0.2.2:8081/register2";
    private RequestQueue requestQueue;
    TextView jsonI;
    String re = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String jwt = "jwt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_decrypt);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        jwt = sharedPreferences.getString("jwt", null);
        Log.d("TOP PREF JWT : ",  jwt);




        //for volley request

        jsonI = (TextView) findViewById(R.id.jsonIn);
        final EditText hello = (EditText) findViewById(R.id.helloText);
        final TextView scramble = (TextView) findViewById(R.id.answer);
        Button enc = (Button)findViewById(R.id.encryptbutton);
        Button dec =  (Button)findViewById(R.id.decryptbutton);


        enc.setOnClickListener(new View.OnClickListener(){

            Keys data;
            Context c = getApplicationContext();
            @Override
            public void onClick(View v){

                Encrypt enc = new Encrypt();
                String message="";//message to return
                Gson gson = new Gson();
                data = gson.fromJson(enc.Enc(hello.getText().toString(),c ), Keys.class);

                scramble.setText(
                        "rsa:   " + data.getRsa() + "\n\n" +
                        "hamc:  " + data.getHmac() + "\n\n" +
                        "ivaes: " + data.getIvaes());
                Log.d("", "onClick: "+ data.getRsa());
            }
        } );

        dec.setOnClickListener(new View.OnClickListener(){

            Context c = getApplicationContext();
            @Override
            public void onClick(View v){
                Encrypt enc = new Encrypt();
                String obj = enc.Enc(hello.getText().toString(),c );
                String hello1 = enc.Dec(obj,c);
                scramble.setText(hello1);
            }
        } );

       //end of the two buttons
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        User requestBodyClass = new User("Archer", "Archer");
        Gson gson = new Gson();
        final String requestBody = gson.toJson(requestBodyClass, User.class);

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
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };

// add the request object to the queue to be executed
        requestQueue.add(reqPost);

        //register();

       // String mRecentAddress = sharedPreferences.getString("jwt", null);
       // Log.d("Shared Pref Location", mRecentAddress);


    }//end onCreate




    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);
        requestQueue.add(request);
    }

    private void register(){
        StringRequest postReq = new StringRequest(Request.Method.POST, ENDPOINTR, onValidRegister, onInvalidRegister){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", "Jill");
                params.put("password", "abcd");
                return params;
            }
        };
        requestQueue.add(postReq);
    }

    private final Response.Listener<String> onValidRegister = new Response.Listener<String>(){
        @Override
        public void onResponse(String response){
            Gson gson = new Gson();
            Register data = gson.fromJson(response, Register.class);
           // Log.d("JWT FROM REGISTR", data.getJwt());
            Log.d("JWT FROM REGISTR", response);
        }
    };

    private final Response.ErrorListener onInvalidRegister = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("PostActivity", "whaaaaa" +response);
           jsonI.setText(response);
            //convert response to object to parse jwt
            editor.putString("jwt", response);
            editor.commit();
            Log.d("jwt" , "JWT PREF IS:  " + jwt);

        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

}
