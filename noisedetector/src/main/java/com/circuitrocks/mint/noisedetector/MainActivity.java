package com.circuitrocks.mint.noisedetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.circuitrocks.mint.noisedetector.sound.NoiseAlertService;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_PERMISSIONS = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        init();
    }

    public void init(){

        //setup buttons to start and stop service
        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Noise","onclick startService");
                Intent serviceIntent = new Intent(MainActivity.this,NoiseAlertService.class);
                MainActivity.this.startService(serviceIntent);
                finish();
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Noise","onclick startService");
                Intent serviceIntent = new Intent(MainActivity.this,NoiseAlertService.class);
                MainActivity.this.stopService(serviceIntent);
            }
        });
    }
    public void checkPermissions() {
        //check permissions used for Android M and above
        if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.WAKE_LOCK
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.INTERNET
                            , Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSIONS);
        } else {
            init();
        }
    }


    @Override //this is called after permissions are asked
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean callForRecheckPermissions = false;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_DENIED) {
                    callForRecheckPermissions = true;
                }
            }
            if (callForRecheckPermissions) {
                checkPermissions();
            } else {
                init();
            }
        }
    }
}
