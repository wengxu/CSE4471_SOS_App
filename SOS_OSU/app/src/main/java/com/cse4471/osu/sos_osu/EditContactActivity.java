package com.cse4471.osu.sos_osu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

public class EditContactActivity extends AppCompatActivity {
    ContactDbAdapter contactDbAdapter;
    Cursor cursor;
    String phoneNum;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        final EditText phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);


        contactDbAdapter =  new ContactDbAdapter(this);
        try {
            contactDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open contactDbAdapter: "+ error.toString());
        }

        Intent intent = getIntent();
        final String editORadd = intent.getStringExtra("edit/add");
        int position = intent.getIntExtra("position", 0) ;
        cursor = contactDbAdapter.getContacts();
        cursor.moveToPosition(position);
        // get the id of contact in database
        final int id = cursor.getInt(cursor.getColumnIndex(contactDbAdapter.KEY_ROWID));

        name = "";
        phoneNum = "";

        // set title based on if adding a new contact or edit existing contact
        // if it's edit existing contact
        if (editORadd.equals("edit")) {
            getSupportActionBar().setTitle("Edit Contact");
            // get data from database
            cursor = contactDbAdapter.getContactById(id);
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(contactDbAdapter.FIRST_NAME));
                phoneNum = cursor.getString(cursor.getColumnIndex(contactDbAdapter.PHONE_NUM));
                firstNameEditText.setText(name);
                phoneEditText.setText(phoneNum);
            }
            // implement delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public  void onClick(View view) {
                    contactDbAdapter.deleteContact(id);
                    //Log.d("the deleted id is ", String.valueOf(id));
                    Toast.makeText(getApplicationContext(),  "Contact Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            // implement save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public  void onClick(View view) {
                    if (! (phoneNum.equals(phoneEditText.getText().toString()) && name.equals(firstNameEditText.getText().toString())) ) {
                        Contact contact = new Contact(firstNameEditText.getText().toString(), "", phoneEditText.getText().toString());
                        contactDbAdapter.updateContact(id, contact.getContentValues());
                        Toast.makeText(getApplicationContext(), "Contact Updated", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });


        }
        // if it's adding new contact
        else if (editORadd.equals("add")) {
            getSupportActionBar().setTitle("Add Contact");
            // hide delete button
            deleteButton.setVisibility(View.GONE);
            firstNameEditText.setText("Name");
            // implement save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(phoneNum.equals(phoneEditText.getText().toString()) && name.equals(firstNameEditText.getText().toString()))) {
                        Contact contact = new Contact(firstNameEditText.getText().toString(), "", phoneEditText.getText().toString());
                        contactDbAdapter.insertContact(contact.getContentValues());
                        Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });
        }

        // implement cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

}
