package com.circuitrocks.mint.noisedetector.sound;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.circuitrocks.mint.noisedetector.MainActivity;
import com.circuitrocks.mint.noisedetector.R;
import com.circuitrocks.mint.noisedetector.sound.SoundMeter;

import java.util.HashMap;
import java.util.Locale;

public class NoiseAlertService extends IntentService {

    private static final int POLL_INTERVAL = 300;
    private static final int notificationID = 0x002;

    private boolean isTalking = false;

    private int mThreshold;

    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();
    private SoundMeter mSensor;

    TextToSpeech ttobj;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {

            double amp = mSensor.getAmplitude();

            if ((amp > mThreshold)) {
                callForHelp();

            }

            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };


    public NoiseAlertService() {
        super("NoiseAlertService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stop();
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NoiseAlert", "Received start id " + startId + ": " + intent);

        // Used to record voice and get amplitude
        mSensor = new SoundMeter();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
        mSleepTask.run();

        initializeApplicationConstants();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void start() {
        Log.i("NoiseAlert", "==== start ===");

        //create notification to show and remain on start of service
        Intent myIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.tank);
        mBuilder.setContentTitle("DTank Noise Detector");
        mBuilder.setContentText("Noise Detector is ongoing");
        mBuilder.setAutoCancel(false);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setOngoing(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());

        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        Log.i("NoiseAlert", "==== Stop Noise Monitoring===");

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();

        //cancels notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationID);


    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 12;

        //initialized text to speech object
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }


    private void callForHelp() { //called when amplitude is greater than threshold
        String status = "Noise detected!";
        Log.d("NoiseAlert",status);

        if(!isTalking) {
            //start activity that looks like a dialog
          /*  Intent i = new Intent();
            i.setClass(this, NoiseAlertDialogActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
          */

            //calls text to speech to talk
            HashMap<String, String> map = new HashMap<String, String>(); //needs hashmap to place ID used for UtteranceProgressListener
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

            ttobj.setLanguage(Locale.UK);
            ttobj.speak("Please observe silence!", TextToSpeech.QUEUE_ADD, map);
        }

        ttobj.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.d("NoiseAlert","setOnUtteranceProgressListener onStart");
                isTalking = true;
            }

            @Override
            public void onDone(String s) {
                Log.d("NoiseAlert","setOnUtteranceProgressListener onDone");
                stop();

                //sleep for 2s before restarting service
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    isTalking = false;
                    start();
                }
            }

            @Override
            public void onError(String s) {

            }
        });
    }
}
