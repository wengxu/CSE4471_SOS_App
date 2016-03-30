package com.cse4471.osu.sos_osu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

public class DisplayContactActivity extends AppCompatActivity {

    ListView listView;
    ContactDbAdapter contactDbAdapter;
    Cursor cursor;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listView = (ListView) findViewById(R.id.listView);
        contactDbAdapter = new ContactDbAdapter(this);
        try {
            contactDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open contactDbAdapter");
        }

        // insert fake data if no data is in the database
        if (contactDbAdapter.getContactSize() == 0) {
            ArrayList<Contact> contacts = new ArrayList<Contact>();
            contacts = Contact.generateFakeContacts();
            for (Contact c : contacts) {
                ContentValues newValue = new ContentValues();
                newValue.put(contactDbAdapter.FIRST_NAME, c.mFirstName);
                newValue.put(contactDbAdapter.LAST_NAME, c.mLastName);
                newValue.put(contactDbAdapter.PHONE_NUM, c.mPhoneNum);
                contactDbAdapter.insertContact(newValue);
            }
        }


        // read data from database
        cursor = contactDbAdapter.getContacts();
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] {ContactDbAdapter.FIRST_NAME, ContactDbAdapter.LAST_NAME},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor!= null) {
            cursor.close();
        }
        if (contactDbAdapter != null) {
            contactDbAdapter.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.add_contact:
                Intent editContactIntent = new Intent(getApplicationContext(), EditContactActivity.class);
                editContactIntent.putExtra("edit/add", "add");
                startActivity(editContactIntent);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
