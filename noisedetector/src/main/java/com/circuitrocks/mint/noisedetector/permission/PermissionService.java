package com.circuitrocks.mint.noisedetector.permission;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.circuitrocks.mint.noisedetector.sound.NoiseAlertService;
import com.circuitrocks.mint.noisedetector.R;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;
import com.permissioneverywhere.PermissionResultCallback;

public class PermissionService extends Service {
    public PermissionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NoiseAlert","PermissionService onCreate");
        PermissionEverywhere.getPermission(this,
                new String[]{Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WAKE_LOCK
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.INTERNET
                        , Manifest.permission.READ_PHONE_STATE},
                1,
                "Noise Alert",
                "This app needs a write permission",
                R.mipmap.ic_launcher)
                .enqueue(new PermissionResultCallback() {
                    @Override
                    public void onComplete(PermissionResponse permissionResponse) {
                        Log.d("NoiseAlert","PermissionService PermissionEverywhere onComplete");
                        Toast.makeText(PermissionService.this, "is Granted " + permissionResponse.isGranted(), Toast.LENGTH_SHORT).show();

                        Intent serviceIntent = new Intent(PermissionService.this,NoiseAlertService.class);
                        PermissionService.this.startService(serviceIntent);
                    }
                });

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("NoiseAlert","PermissionService onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NoiseAlert","PermissionService onStartCommand");

        return super.onStartCommand(intent, flags, startId);

    }
}