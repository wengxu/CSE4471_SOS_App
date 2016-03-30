package com.cse4471.osu.sos_osu;

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

    public static ArrayList<Contact> generateFakeContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (int i = 0; i < 10; i++) {
            contacts.add(new Contact("First Name " + i, "Last Name " + i, "123456789" + i));
        }
        return contacts;
    }

}
