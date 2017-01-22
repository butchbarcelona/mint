package com.circuitrocks.mint.noisedetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mbarcelona on 1/17/17.
 */

public class NoiseAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NoiseAlertService.class);
        context.startService(service);
    }
}