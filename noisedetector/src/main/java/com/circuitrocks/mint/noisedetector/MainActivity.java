package com.circuitrocks.mint.noisedetector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent serviceIntent = new Intent(MainActivity.this,NoiseAlertService.class);
                MainActivity.this.startService(serviceIntent);
                finish();
            }
        });


        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent serviceIntent = new Intent(MainActivity.this,NoiseAlertService.class);
                MainActivity.this.stopService(serviceIntent);
            }
        });
    }
}
