package com.cse4471.osu.sos_osu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class EditContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String editORadd = intent.getStringExtra("edit/add");
        // set title based on if adding a new contact or edit existing contact
        if (editORadd.equals("edit")) {
            getSupportActionBar().setTitle("Edit Contact");

        }
        else if (editORadd.equals("add")) {
            getSupportActionBar().setTitle("Add Contact");

        }

    }

}
