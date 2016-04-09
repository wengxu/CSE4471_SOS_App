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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        EditText phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);


        contactDbAdapter =  new ContactDbAdapter(this);
        try {
            contactDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open contactDbAdapter");
        }

        Intent intent = getIntent();
        String editORadd = intent.getStringExtra("edit/add");
        int position = intent.getIntExtra("position", 0) ;
        cursor = contactDbAdapter.getContacts();
        cursor.moveToPosition(position);
        // get the id of contact in database
        final int id = cursor.getInt(cursor.getColumnIndex(contactDbAdapter.KEY_ROWID));

        //Log.d("id", String.valueOf(id));
        // set title based on if adding a new contact or edit existing contact

        if (editORadd.equals("edit")) {
            getSupportActionBar().setTitle("Edit Contact");
            // get data from database
            cursor = contactDbAdapter.getContactById(id);
            if (cursor.moveToFirst()) {
                firstNameEditText.setText(cursor.getString(cursor.getColumnIndex(contactDbAdapter.FIRST_NAME)));
                phoneEditText.setText(cursor.getString(cursor.getColumnIndex(contactDbAdapter.PHONE_NUM)));
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


        }
        else if (editORadd.equals("add")) {
            getSupportActionBar().setTitle("Add Contact");

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
