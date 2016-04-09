package com.cse4471.osu.sos_osu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class DisplayContactActivity extends AppCompatActivity {

    ListView listView;
    ContactDbAdapter contactDbAdapter;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    static final int PICK_CONTACT=1;

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
                new String[] {ContactDbAdapter.FIRST_NAME, ContactDbAdapter.PHONE_NUM},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        listView.setAdapter(adapter);


        // enable list item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public  void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editContactIntent = new Intent(getApplicationContext(), EditContactActivity.class);
                editContactIntent.putExtra("edit/add", "edit");
                editContactIntent.putExtra("position", position);
                startActivity(editContactIntent);
            }
        });
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
    public  void onResume() {
        super.onResume();
        refreshView();
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

            case R.id.add_contact:
                /*
                Intent editContactIntent = new Intent(getApplicationContext(), EditContactActivity.class);
                editContactIntent.putExtra("edit/add", "add");
                startActivity(editContactIntent);
                */
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                //pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // process data from contact picker
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent backIntent) {
        super.onActivityResult(reqCode, resultCode, backIntent);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri  = backIntent.getData();
                    String[] mProjection = {
                            ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                            ContactsContract.Contacts._ID,

                    };
                    Cursor pickCursor = getContentResolver().query(contactUri, null, null, null, null);
                    if (pickCursor.moveToFirst()) {
                        Log.d(ContactsContract.CommonDataKinds.Phone.DATA1, pickCursor.getString(pickCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1)));
                        Contact newContact = new Contact(pickCursor.getString(pickCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                                null, pickCursor.getString(pickCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1)));
                        contactDbAdapter.insertContact(newContact.getContentValues());
                        /*
                        for (int i = 0; i < pickCursor.getColumnCount(); i++) {
                            Log.d(pickCursor.getColumnName(i), "         " + pickCursor.getString(i));
                        }
                        */
                        // update view
                        //refreshView();
                        // display data added message
                        Toast.makeText(getApplicationContext(), newContact.mFirstName +  " added to contact", Toast.LENGTH_SHORT).show();

                    }
                    pickCursor.close();
                }
        }

    }

    public void refreshView() {
        adapter.notifyDataSetChanged();
        cursor = contactDbAdapter.getContacts();
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] {ContactDbAdapter.FIRST_NAME, ContactDbAdapter.PHONE_NUM},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        listView.setAdapter(adapter);
    }


    }
