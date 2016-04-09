package com.cse4471.osu.sos_osu;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by xuweng on 3/28/16.
 */
public class User {
    int mId;
    String mMessage;

    // constructors
    public User() {

    }

    public User(String message) {
        this.mMessage = message;
    }

    public ContentValues getContentValues() {
        ContentValues newValue = new ContentValues();
        newValue.put(UserDbAdapter.MESSAGE, mMessage);
        return newValue;
    }

    public static User generateUser() {
        User user = new User("Emergency Situation. Need help!");
        return user;
    }


}
