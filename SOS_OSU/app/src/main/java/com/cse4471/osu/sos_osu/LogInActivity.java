package com.cse4471.osu.sos_osu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
// enables user to enter encryption key
public class LogInActivity extends AppCompatActivity {

    EditText passwrodText;
    Button OKbutton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        passwrodText = (EditText) findViewById(R.id.passwordEditText);
        OKbutton = (Button) findViewById(R.id.OKbutton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // update password from user input, the password will be used encrypt and decrypt data.
        OKbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void  onClick(View view) {
                String password  = passwrodText.getText().toString();
                Log.d("hash code ", String.valueOf(password.hashCode()));
                ContactDbAdapter.password = password.hashCode();
                Intent contactIntent = new Intent(getApplicationContext(), DisplayContactActivity.class);
                startActivity(contactIntent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void  onClick(View view) {
                finish();
            }
        });




    }

}
