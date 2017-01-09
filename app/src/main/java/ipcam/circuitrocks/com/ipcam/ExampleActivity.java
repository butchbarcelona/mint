package ipcam.circuitrocks.com.ipcam;

/**
 * Created by mbarcelona on 1/7/17.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import ipcam.circuitrocks.com.ipcam.ble.BlunoLibrary;


public class ExampleActivity extends BlunoLibrary implements View.OnClickListener
{
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;

    ImageButton btnUp, btnDown, btnLeft, btnRight;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        onCreateProcess();
        serialBegin(115200);

        requestPermission();
      /*  else{
            init();
        }*/


    }

    public void requestPermission(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if( ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission( this,
                    Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions( this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION
                                , Manifest.permission.ACCESS_FINE_LOCATION }, 1 );
            }else{
                init();
            }
        }else{
            init();
        }
    }

    public void init(){
        buttonScanOnClickProcess();

        btnUp = (ImageButton) findViewById(R.id.btn_up);
        btnDown = (ImageButton) findViewById(R.id.btn_down);
        btnLeft = (ImageButton) findViewById(R.id.btn_left);
        btnRight = (ImageButton) findViewById(R.id.btn_right);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        // Save the web view
        webView = (VideoEnabledWebView)findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new InsideWebViewClient());

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        webView.loadUrl("http://192.168.1.37:8080/jsfs.html");

    }

    @Override
    public void onSerialReceived(String bleData) {
        bleData = bleData.trim().toUpperCase();
        Log.e("bluno", "data:"+ bleData);


        String data = ((bleData.length() > 0 && !bleData.contains("N"))?bleData.substring(1):"0.0");
/*
        if(bleData.length()> 0) {
            try {
                switch (bleData.charAt(0)) {
                    case 'A':
                        rpm = Float.valueOf(data);
                        mph = rpm * multiplier;
                        break;
                    case 'B':
                        torque = Float.valueOf(data) * torqueMultiplier;
                        break;
                    case 'C':
                        temp = Float.valueOf(data);
                        graphFragment.setfTemp(temp);
                        break;
                    case 'D':
                        humidity = Float.valueOf(data);
                        graphFragment.setfHumidity(humidity);
                        break;
                    case 'E':
                        engineTemp = Float.valueOf(data);
                        graphFragment.setfEngineTemp(engineTemp);
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btn_down:
                serialSend("s");
                break;
            case R.id.btn_up:
                serialSend("w");
                break;
            case R.id.btn_left:
                serialSend("a");
                break;
            case R.id.btn_right:
                serialSend("d");
                break;
        }
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult( int requestCode,
                                            String permissions[], int[] grantResults ) {
        switch( requestCode ) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    Log.d( "butch", "coarse location permission granted" );
                    init();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder( this );
                    builder.setTitle( "Functionality limited" );
                    builder.setMessage( "Since location access has not been granted, this app will not be able to discover beacons when in the background." );
                    builder.setPositiveButton( android.R.string.ok, null );
                    builder.setOnDismissListener( new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss( DialogInterface dialog ) {
                        }

                    } );
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed())
        {
            if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }


    protected void onResume() {
        super.onResume();
        onResumeProcess();                                                        //onResume Process by BlunoLibrary

    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary

    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
    }

}