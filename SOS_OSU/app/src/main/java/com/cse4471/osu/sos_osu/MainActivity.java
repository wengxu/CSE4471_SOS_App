package com.cse4471.osu.sos_osu;

import android.Manifest;
import android.content.Context;

import android.content.Intent;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.view.MenuItem;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    ContactDbAdapter contactDbAdapter;

    private GoogleApiClient client;
    EditText messageText;
    UserDbAdapter userDbAdapter;
    Cursor cursor;
    TextView locationText;

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return super.checkUriPermission(uri, pid, uid, modeFlags);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        userDbAdapter = new UserDbAdapter(this);
        messageText = (EditText) findViewById(R.id.messageText);
        locationText = (TextView) findViewById(R.id.locationTextView);

        try {
            userDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open userDbAdapter");
        }


        contactDbAdapter = new ContactDbAdapter(this);
        try {
            contactDbAdapter.open();
        } catch (SQLException error) {
            Log.e("mytag", "Error open contactDbAdapter");
        }
        cursor = contactDbAdapter.getContacts();


        final Button sos = (Button) findViewById(R.id.redbutton);
        final Button finish = (Button) findViewById(R.id.greenbutton);

        final CountDownTimer timer = new CountDownTimer(5999, 100) {
            public void onTick(long millisUntilFinished) {
                sos.setText("" + ((int) (millisUntilFinished) / 1000));
            }
            public void onFinish() {
                sos.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
                finish.setText("finish");
                SmsManager smsManager = SmsManager.getDefault();
                if(cursor.moveToFirst()){
                    do{
                        String number=cursor.getString(cursor.getColumnIndex(contactDbAdapter.PHONE_NUM));
                        smsManager.sendTextMessage(number, null, "test", null, null);
                    }while(cursor.moveToNext());
                }
            }
        };

        sos.setTag(1);
        sos.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        final int status = (Integer) v.getTag();
                        if (status != 1) {
                            sos.setText("sos");
                            sos.setTag(1);
                            timer.cancel();
                        } else {
                            sos.setTag(0);
                            timer.start();
                        }

                    }

                }
        );
        finish.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        sos.setVisibility(View.VISIBLE);
                        finish.setVisibility(View.GONE);
                    }

                }
        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            case R.id.contact:
                Intent contactIntent = new Intent(getApplicationContext(), DisplayContactActivity.class);
                startActivity(contactIntent);
                return true;
            case R.id.message:
                Intent messageIntent = new Intent(getApplicationContext(), DisplayMessageActivity.class);
                startActivity(messageIntent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse4471.osu.sos_osu/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public  void onResume() {
        super.onResume();
        // refresh user message
        cursor = userDbAdapter.getUsers();
        if (cursor.moveToFirst()) {
            messageText.setText(cursor.getString(cursor.getColumnIndex(userDbAdapter.MESSAGE)));
        }
        // Acquire a reference to the system Location Manager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                locationText.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(loc != null) {
            // messageText.setText("Latitude:" + loc.getLatitude() + ", Longitude:" + loc.getLongitude());
            locationText.setText("Latitude:" + loc.getLatitude() + ", Longitude:" + loc.getLongitude());
        }


    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse4471.osu.sos_osu/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }

}
