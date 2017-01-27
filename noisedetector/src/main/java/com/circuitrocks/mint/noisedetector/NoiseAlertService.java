package com.circuitrocks.mint.noisedetector;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
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
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp();
                //Log.i("Noise", "==== onCreate ===");

            }

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // Used to record voice
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
        Log.i("Noise", "==== start ===");


        //Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Noise Detector");
        mBuilder.setContentText("Noise Detector service started.");
        mBuilder.setAutoCancel(false);
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
        Log.i("Noise", "==== Stop Noise Monitoring===");


        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        updateDisplay("stopped...", 0.0);


    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 10;

         ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
    }

    private void updateDisplay(String status, double signalEMA) {
        //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
        Log.d("Noise",status);
    }






    private void callForHelp() {
        String status = "Noise detected!";
        Log.d("Noise",status);

        //start activity that looks like a dialog
      /*  Intent i = new Intent();
        i.setClass(this, NoiseAlertDialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
*/
        if(!isTalking) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            ttobj.setLanguage(Locale.UK);
            ttobj.speak("Please keep quiet!", TextToSpeech.QUEUE_ADD, map);
        }


        ttobj.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.d("Noise","setOnUtteranceProgressListener onStart");
                isTalking = true;
            }

            @Override
            public void onDone(String s) {
                Log.d("Noise","setOnUtteranceProgressListener onDone");

                stop();
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

    public void showDialog(Context ctx, String message, String okButton
            , DialogInterface.OnClickListener positiveListener) {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(okButton, positiveListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        alertDialog.show();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

}
