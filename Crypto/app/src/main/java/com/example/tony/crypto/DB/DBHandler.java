package com.example.tony.crypto.DB;

/**
 * Created by tonyd on 3/29/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tony.crypto.POJOS.Friend;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "friendManager1";

    // Contacts table name
    private static final String TABLE_KEYS = "keyHolder1";

    // Contacts Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_PU_KEY = "public_key";
    private static final String KEY_PR_KEY = "private_key";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //deletes the table, but not the db
    public void dropT(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_KEYS);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXIST " + TABLE_KEYS);
        String CREATE_KEYS_TABLE = "CREATE TABLE " + TABLE_KEYS + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_PU_KEY + " TEXT,"
                + KEY_PR_KEY + " TEXT" + ")";
        db.execSQL(CREATE_KEYS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEYS);

        // Create tables again
        onCreate(db);
    }

    // Adding new friend, only private key
    public void addFriend(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();
        String n = null;
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, friend.getName()); // friend Name
        values.put(KEY_PU_KEY, n); // friend key
        values.put(KEY_PR_KEY, friend.getPrivateKey()); // friend key

        // Inserting Row
        db.insert(TABLE_KEYS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new myself
    public void addMe(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, friend.getName()); // friend Name
        values.put(KEY_PU_KEY, friend.getPublicKey()); // public key
        values.put(KEY_PR_KEY, friend.getPrivateKey()); // private key

        // Inserting Row
        db.insert(TABLE_KEYS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single friend
    public Friend getFriend(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_KEYS, new String[] { KEY_NAME,
                        KEY_PU_KEY, KEY_PR_KEY }, KEY_NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Friend friend = new Friend(cursor.getString(0),cursor.getString(1), cursor.getString(2));
        // return contact
        return friend;
    }

    // Getting All Contacts
    public ArrayList<Friend> getAllFriends() {
        ArrayList<Friend> friendList = new ArrayList<Friend>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_KEYS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Friend friend = new Friend(cursor.getString(0),cursor.getString(1), cursor.getString(2));
                friendList.add(friend);
            } while (cursor.moveToNext());
        }
        // return contact list
        return friendList;
    }

//    public int updateFriend(Friend friend) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, friend.getName());
//        values.put(KEY_KEY, friend.getKey());
//
//        // updating row
//        return db.update(TABLE_KEYS, values, KEY_NAME + " = ?",
//                new String[] { String.valueOf(friend.getName()) });
//    }

    // Deleting single contact
    public void deleteContact(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KEYS, KEY_NAME + " = ?",
                new String[] { String.valueOf(friend.getName()) });
        db.close();
    }

    // Getting friends count
    public int getFriendCount() {
        String countQuery = "SELECT  * FROM " + TABLE_KEYS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
