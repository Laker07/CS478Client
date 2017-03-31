package com.example.tony.crypto.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tony.crypto.POJOS.Friend;
import com.example.tony.crypto.R;

import java.util.ArrayList;

/**
 * Created by tonyd on 3/29/2017.
 */

public class FriendAdapter extends ArrayAdapter<Friend>  {

    public FriendAdapter(Context context, int resource, ArrayList<Friend> data){
        super(context, resource, data);
    }



    public View getView(int position, View view, ViewGroup parent) {
        //Get the instance of our chat
        Friend friend = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE );

        //Create new list item
        View rowView = inflater.inflate(R.layout.list_friends, null, true);

        //Get UI objects
        //ImageView profilePic = (ImageView) rowView.findViewById(R.id.profile_pic_imageview);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView messageView = (TextView) rowView.findViewById(R.id.message);

        //Set image profile picture
        //profilePic.setImageDrawable(getContext().getResources().getDrawable(chat.getProfilePic()));

        //Set text into TextViews
        nameView.setText(friend.getName());
        messageView.setText("Something goes here..");

        return rowView;
    }
}
