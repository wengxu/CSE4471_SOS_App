package com.cse4471.osu.sos_osu;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button sos= (Button)findViewById(R.id.button);
        final CountDownTimer timer= new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                sos.setText("" + (millisUntilFinished / 1000));
            }
            public void onFinish() {
                sos.setText("finish");
                sos.setTextSize(50);
            }
        };

        sos.setTag(1);
        sos.setOnClickListener(
                new Button.OnClickListener(){
                    public void  onClick(View v){
                        final int status =(Integer) v.getTag();
                        if (status!=1){
                            sos.setText("sos");
                            sos.setTag(1);
                            timer.cancel();
                        }else{
                            sos.setTag(0);
                            timer.start();
                        }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
