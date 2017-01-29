package com.circuitrocks.mint.noisedetector.sound;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.circuitrocks.mint.noisedetector.R;

/**
 * Created by mbarcelona on 1/21/17.
 */

public class NoiseAlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Button btnFinish = (Button) findViewById(R.id.btn_cancel);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
