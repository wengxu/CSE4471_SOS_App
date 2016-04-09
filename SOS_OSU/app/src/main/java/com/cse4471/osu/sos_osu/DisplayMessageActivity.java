package com.cse4471.osu.sos_osu;

import android.content.ContentValues;
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
import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {
    EditText editText;
    UserDbAdapter userDbAdapter;
    Cursor cursor;
    String message;
    int id;
    Button confirmButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.messageText);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        userDbAdapter = new UserDbAdapter(this);

        try {
            userDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open userDbAdapter");
        }
        //cursor = userDbAdapter.getUsers();


        // insert data if no data is in the database
        if (userDbAdapter.getUserSize() == 0) {
            User user = User.generateUser();
            userDbAdapter.insertUser(user.getContentValues());
        }

        // read data from database
        cursor = userDbAdapter.getUsers();
        if (cursor.moveToFirst()) {
            message = cursor.getString(cursor.getColumnIndex(userDbAdapter.MESSAGE));
            id = cursor.getInt(cursor.getColumnIndex(userDbAdapter.KEY_ROWID));
            Log.d("id is ", String.valueOf(id));
        }

        editText.setText(message);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.equals(editText.getText().toString())) {
                    User user = new User(editText.getText().toString());
                    userDbAdapter.updateUser(id, user.getContentValues());
                    Toast.makeText(getApplicationContext(), "Message updated", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                finish();
            }
        });

    }

}
