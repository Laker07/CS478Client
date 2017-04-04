package com.example.tony.crypto.Chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tony.crypto.EncDec.Encrypt;
import com.example.tony.crypto.POJOS.GetMessages;
import com.example.tony.crypto.POJOS.Message;
import com.example.tony.crypto.POJOS.ServerResponse;
import com.example.tony.crypto.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

public class Messenger extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String ENDPOINTR = "http://10.0.2.2:8080/sendMessage";
    private static final String ENDPOINTP = "http://10.0.2.2:8080/getMessages";

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;
    ServerResponse res;
    GetMessages getMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("jwt : ", sharedPreferences.getString("jwt", null));
        Log.d("name : ", sharedPreferences.getString("name", null));
        Log.d("current: ", sharedPreferences.getString("currentConversation", null));

        Button submit = (Button)findViewById(R.id.submitMessage);
        Button pull = (Button)findViewById(R.id.pullMessage);
        final TextView myMsg= (TextView)findViewById(R.id.inputMessage);
        final TextView msg = (TextView) findViewById(R.id.editText);
        final TextView recMsg = (TextView)findViewById(R.id.receivedMsg);
        requestQueue = Volley.newRequestQueue(getApplicationContext());




        submit.setOnClickListener(new View.OnClickListener(){
            Context context = getApplicationContext();
            @Override
            public void onClick(View v) {
                myMsg.setText(msg.getText().toString());
                JsonObjectRequest reqPost = new JsonObjectRequest(Request.Method.POST, ENDPOINTR, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Gson gson = new Gson();
                            res = gson.fromJson(response.toString(), ServerResponse.class);

                            //if login error then toast message to screen
                            if(res.getResponse().equals("Error")) {
                                Log.d("error res: ", res.getMessage());

                            }else {

                                Log.d("res : ", res.getMessage());

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
                        headers.put("Content-Type", "application/json");
                        //headers.put("Authorization", "Bearer " + jwtToken);
                        headers.put("token", jwtToken);
                        return headers;
                    }

                    // @Override

                    @Override
                           public byte[] getBody() {
                      try {
                           SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                           Encrypt enc = new Encrypt();
                           String encMsg = enc.Enc(msg.getText().toString(), context);

                           Message requestBodyClass = new Message(sharedPreferences.getString("name", null),
                                    sharedPreferences.getString("currentConversation",null), encMsg, format.format(new Timestamp(System.currentTimeMillis())));
                            Gson gson = new Gson();
                            final String requestBody = gson.toJson(requestBodyClass, Message.class);

                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    null , "utf-8");
                            return null;
                        }
                     }
                };
                reqPost.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                RequestQueue rQueue = Volley.newRequestQueue(Messenger.this);
                requestQueue.add(reqPost);
            }
        } );

        pull.setOnClickListener(new View.OnClickListener(){

            Context context = getApplicationContext();
            @Override
            public void onClick(View v) {

                Map<String, String> params = new HashMap();
                params.put("first_param", "st");
                params.put("second_param", "st");
                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest getPost = new JsonObjectRequest(Request.Method.GET, ENDPOINTP, parameters,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
//
                                try {

                                    //verify 200
                                    String respon = response.getString("response");
                                    //VolleyLog.v("Response:%n %s", response.toString(4));
                                    Log.d("all", response.toString());
                                    Log.d("count", ""+response.getString("messagecount"));
                                    Log.d("response ", response.getString("response") );

                                    if(response.getString("response").equals("Error")){
                                        //toast error
                                    }else if(response.getString("response").matches("Success")){
                                        String mes="";// = childJSONObject.getString("message");
                                        String sen="";// = childJSONObject.getString("sender");
                                        String dat="";// = childJSONObject.getString("timestamp");
                                        JSONArray jsonMainArr = response.getJSONArray("messages");
                                        for (int i = 0; i < jsonMainArr.length(); i++) {  // **line 2**
                                            JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
                                            mes = childJSONObject.getString("message");
                                            sen = childJSONObject.getString("sender");
                                            dat = childJSONObject.getString("timestamp");
                                        }

                                        System.out.println("SYSTEM OUT ............... " + jsonMainArr.get(0));
                                        System.out.println("SYSTEM OUT ............... " + mes);
                                        Encrypt enc = new Encrypt();
                                        String encMsg = enc.Dec(mes, context);
                                        System.out.println("Decrypted message " + encMsg);

                                        recMsg.setText(encMsg);
                                    }



                                } catch (JSONException e) {
                                    Log.d("JSONException msg: ", e.getMessage());
                                    Context context = getApplicationContext();
                                    CharSequence text = "message error";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Volley Error after response: ", error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String jwtToken = sharedPreferences.getString("jwt", null);
                        Log.d("JWT AT HEADER: ", jwtToken);
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("token", jwtToken);
                        return headers;
                    }
                };
                getPost.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(getPost);
            }
        } );




    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
