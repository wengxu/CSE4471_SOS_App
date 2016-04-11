package com.cse4471.osu.sos_osu;

import android.Manifest;
import android.content.Context;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {


    TextView txtLat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);

            return;
        }
        txtLat = (TextView) findViewById(R.id.textview1);
        // Acquire a reference to the system Location Manager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        }

        txtLat = (TextView) findViewById(R.id.textview1);
        txtLat.setText("Latitude:" + ", Longitude:");
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(loc != null) {
            txtLat.setText("Latitude:" + loc.getLatitude() + ", Longitude:" + loc.getLongitude());
        }


        final Button sos= (Button)findViewById(R.id.redbutton);
        final Button finish= (Button)findViewById(R.id.greenbutton);

        final CountDownTimer timer= new CountDownTimer(5999, 1000) {

            public void onTick(long millisUntilFinished) {
                sos.setText("" + ((int) (millisUntilFinished) / 1000));
            }

            public void onFinish() {
                sos.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
                finish.setText("finish");

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("6143026390", null, "test", null, null);

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

}
