package com.circuitrocks.mint.noisedetector.permission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mbarcelona on 1/17/17.
 */

public class ServiceStarter extends BroadcastReceiver {
    public ServiceStarter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NoiseAlert","ServiceStarter onReceive");
        Intent serviceIntent = new Intent(context,PermissionService.class);
        context.startService(serviceIntent);
    }

}
