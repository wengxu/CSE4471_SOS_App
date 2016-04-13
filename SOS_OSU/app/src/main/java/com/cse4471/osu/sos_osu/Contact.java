package com.cse4471.osu.sos_osu;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by xuweng on 3/28/16.
 */
public class Contact {
    int mId;
    String mFirstName;
    String mLastName;
    String mPhoneNum;

    // constructors
    public Contact() {

    }

    public Contact(String firstName, String lastName, String phoneNum) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhoneNum = phoneNum;
    }

    public ContentValues getContentValues() {
        ContentValues newValue = new ContentValues();
        newValue.put(ContactDbAdapter.FIRST_NAME, mFirstName);
        newValue.put(ContactDbAdapter.LAST_NAME, mLastName);
        newValue.put(ContactDbAdapter.PHONE_NUM, mPhoneNum);
        return newValue;
    }

    public ContentValues getContentValuesWithEncoding() {
        ContentValues newValue = new ContentValues();
        newValue.put(ContactDbAdapter.FIRST_NAME, mFirstName);
        newValue.put(ContactDbAdapter.LAST_NAME, mLastName);
        newValue.put(ContactDbAdapter.PHONE_NUM, mPhoneNum);
        return newValue;
    }


    public static ArrayList<Contact> generateFakeContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (int i = 0; i < 3; i++) {
            contacts.add(new Contact("First Name " + i, "Last Name " + i, "123456789" + i));
        }
        return contacts;
    }

}
