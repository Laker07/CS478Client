
/**
 * Anthony Martinez
 * CECS 478
 * Prof Dr. Aliasgari
 * Small android app to show/test enc/dec

 */

package com.example.tony.crypto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tony.crypto.Chat.Message;
import com.example.tony.crypto.EncDec.*;
import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import static android.R.attr.type;

public class EncryptDecrypt extends AppCompatActivity {

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_decrypt);

        //temp test userid


        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>("tony", null);
        messagesList.setAdapter(adapter);
        //initMessageAdapter();


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



    }


  // private void initMessagesAdapter(){
//        ImageLoader imageLoader = new ImageLoader() {
//            @Override
//            public void loadImage(ImageView imageView, String url) {
//                Picasso.with(MessagesListActivity.this).load(url).into(imageView);
//            }
//        };

//        if (type == ChatSamplesListAdapter.ChatSample.Type.CUSTOM_LAYOUT) {
//            MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
//            holdersConfig.setIncomingLayout(R.layout.item_custom_incoming_message);
//            holdersConfig.setOutcomingLayout(R.layout.item_custom_outcoming_message);
//            adapter = new MessagesListAdapter<>("0", holdersConfig, imageLoader);
//            adapter.setOnMessageLongClickListener(new MessagesListAdapter.OnMessageLongClickListener<MessagesListFixtures.Message>() {
//                @Override
//                public void onMessageLongClick(MessagesListFixtures.Message message) {
//                    //Yor custom long click handler
//                    Toast.makeText(MessagesListActivity.this,
//                            R.string.on_log_click_message, Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else if (type == ChatSamplesListAdapter.ChatSample.Type.CUSTOM_VIEW_HOLDER) {
//            MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
//            holdersConfig.setIncoming(CustomIncomingMessageViewHolder.class, R.layout.item_custom_holder_incoming_message);
//            holdersConfig.setOutcoming(CustomOutcomingMessageViewHolder.class, R.layout.item_custom_holder_outcoming_message);
//            adapter = new MessagesListAdapter<>("0", holdersConfig, imageLoader);
//            adapter.setOnMessageLongClickListener(new MessagesListAdapter.OnMessageLongClickListener<MessagesListFixtures.Message>() {
//                @Override
//                public void onMessageLongClick(MessagesListFixtures.Message message) {
//                    //Yor custom long click handler
//                    Toast.makeText(MessagesListActivity.this,
//                            R.string.on_log_click_message, Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            adapter = new MessagesListAdapter<>("0", imageLoader);
//            adapter.enableSelectionMode(this);
//        }
//
//        adapter.addToStart(new MessagesListFixtures.Message(), false);
//
//        adapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                if (totalItemsCount < 50) {
//                    loadMessages();
//                }
//            }
//        });
//
//        messagesList.setAdapter(adapter);
//    }



}
