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
import android.widget.EditText;

import java.sql.SQLException;

public class EditContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        EditText phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        ContactDbAdapter contactDbAdapter;

        contactDbAdapter =  new ContactDbAdapter(this);
        try {
            contactDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open contactDbAdapter");
        }

        Intent intent = getIntent();
        String editORadd = intent.getStringExtra("edit/add");
        int id = intent.getIntExtra("position", 0) + 1;

        // set title based on if adding a new contact or edit existing contact

        if (editORadd.equals("edit")) {
            getSupportActionBar().setTitle("Edit Contact");
            // get data from database
            Cursor cursor = contactDbAdapter.getContactById(id);
            if (cursor.moveToFirst()) {
                firstNameEditText.setText(cursor.getString(cursor.getColumnIndex(contactDbAdapter.FIRST_NAME)));
                phoneEditText.setText(cursor.getString(cursor.getColumnIndex(contactDbAdapter.PHONE_NUM)));
            }

        }
        else if (editORadd.equals("add")) {
            getSupportActionBar().setTitle("Add Contact");

        }


    }

}
